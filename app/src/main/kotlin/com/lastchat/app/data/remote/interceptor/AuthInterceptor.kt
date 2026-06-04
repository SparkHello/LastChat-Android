package com.lastchat.app.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private var apiKey: String) : Interceptor {

    fun setApiKey(newKey: String) {
        apiKey = newKey
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        if (apiKey.isNotBlank()) {
            request.addHeader("Authorization", "Bearer $apiKey")
        }
        return chain.proceed(request.build())
    }
}
