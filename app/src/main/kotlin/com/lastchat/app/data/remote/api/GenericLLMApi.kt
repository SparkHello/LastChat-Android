package com.lastchat.app.data.remote.api

import com.lastchat.app.data.remote.dto.common.ChatCompletionRequest
import com.lastchat.app.data.remote.dto.common.ChatCompletionResponse
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Streaming
import retrofit2.http.Url

interface GenericLLMApi {

    @POST
    suspend fun chatCompletion(
        @Url url: String,
        @Body request: ChatCompletionRequest
    ): ChatCompletionResponse

    @POST
    @Streaming
    suspend fun chatCompletionStream(
        @Url url: String,
        @Body request: ChatCompletionRequest
    ): ResponseBody
}
