package com.lastchat.app.service

import com.lastchat.app.data.model.Message
import com.lastchat.app.data.model.MessageRole
import com.lastchat.app.data.model.ProviderConfig
import com.lastchat.app.data.model.ProviderType
import com.lastchat.app.data.remote.api.OpenAIApi
import com.lastchat.app.data.remote.dto.common.ChatCompletionRequest
import com.lastchat.app.data.remote.dto.common.ChatMessageDto
import com.lastchat.app.data.remote.dto.openai.OpenAIStreamChunk
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

class LLMService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = false
    }

    private fun createOkHttpClient(provider: ProviderConfig): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                if (provider.apiKey.isNotBlank()) {
                    if (provider.type == ProviderType.GOOGLE) {
                        request.url(
                            chain.request().url.newBuilder()
                                .addQueryParameter("key", provider.apiKey)
                                .build()
                        )
                    } else {
                        request.addHeader("Authorization", "Bearer ${provider.apiKey}")
                    }
                }
                if (provider.type == ProviderType.ANTHROPIC) {
                    request.addHeader("anthropic-version", "2023-06-01")
                }
                chain.proceed(request.build())
            }
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    private fun createApi(provider: ProviderConfig): OpenAIApi {
        val client = createOkHttpClient(provider)
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(provider.baseUrl)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
        return retrofit.create(OpenAIApi::class.java)
    }

    private fun messagesToDto(messages: List<Message>): List<ChatMessageDto> {
        return messages.map { msg ->
            when (msg.role) {
                MessageRole.SYSTEM -> ChatMessageDto("system", msg.content)
                MessageRole.USER -> ChatMessageDto("user", msg.content)
                MessageRole.ASSISTANT -> ChatMessageDto("assistant", msg.content)
            }
        }
    }

    suspend fun sendMessage(
        messages: List<Message>,
        provider: ProviderConfig,
        model: String
    ): String {
        val api = createApi(provider)
        val request = ChatCompletionRequest(
            model = model,
            messages = messagesToDto(messages)
        )
        return try {
            val response = api.chatCompletion(request)
            response.choices.firstOrNull()?.message?.content ?: "No response"
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun sendMessageStream(
        messages: List<Message>,
        provider: ProviderConfig,
        model: String,
        onToken: (String) -> Unit,
        onComplete: () -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val api = createApi(provider)
        val request = ChatCompletionRequest(
            model = model,
            messages = messagesToDto(messages),
            stream = true
        )
        try {
            val responseBody = api.chatCompletionStream(request)
            parseStreamResponse(responseBody, onToken, onComplete)
        } catch (e: Exception) {
            onError(e)
        }
    }

    private suspend fun parseStreamResponse(
        responseBody: ResponseBody,
        onToken: (String) -> Unit,
        onComplete: () -> Unit
    ) {
        try {
            responseBody.source().use { source ->
                while (!source.exhausted()) {
                    val line = source.readUtf8Line() ?: break
                    if (line.startsWith("data: ")) {
                        val jsonStr = line.substring(6)
                        if (jsonStr == "[DONE]") {
                            onComplete()
                            return
                        }
                        try {
                            val chunk = json.decodeFromString(OpenAIStreamChunk.serializer(), jsonStr)
                            val content = chunk.choices.firstOrNull()?.delta?.content ?: ""
                            if (content.isNotEmpty()) {
                                onToken(content)
                            }
                            if (chunk.choices.firstOrNull()?.finishReason != null) {
                                onComplete()
                                return
                            }
                        } catch (_: Exception) {
                            // Skip malformed chunks
                        }
                    }
                }
                onComplete()
            }
        } catch (e: Exception) {
            onComplete()
        }
    }

    suspend fun fetchModels(provider: ProviderConfig): List<String> {
        return try {
            val api = createApi(provider)
            val response = api.listModels()
            response.data.map { it.id }
        } catch (e: Exception) {
            provider.models.ifEmpty { listOf("default-model") }
        }
    }
}
