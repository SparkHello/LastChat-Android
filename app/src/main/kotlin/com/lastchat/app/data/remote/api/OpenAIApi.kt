package com.lastchat.app.data.remote.api

import com.lastchat.app.data.remote.dto.common.ChatCompletionRequest
import com.lastchat.app.data.remote.dto.common.ChatCompletionResponse
import com.lastchat.app.data.remote.dto.common.ModelsResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Streaming

interface OpenAIApi {

    @POST("chat/completions")
    suspend fun chatCompletion(@Body request: ChatCompletionRequest): ChatCompletionResponse

    @POST("chat/completions")
    @Streaming
    suspend fun chatCompletionStream(@Body request: ChatCompletionRequest): ResponseBody

    @GET("models")
    suspend fun listModels(): ModelsResponse
}
