package com.lastchat.app.data.model

enum class LLMProvider(
    val displayName: String,
    val defaultBaseUrl: String,
    val defaultModels: List<String>
) {
    OPENAI(
        displayName = "OpenAI",
        defaultBaseUrl = "https://api.openai.com/v1/",
        defaultModels = listOf("gpt-4o", "gpt-4o-mini", "gpt-4-turbo", "gpt-3.5-turbo")
    ),
    ANTHROPIC(
        displayName = "Anthropic",
        defaultBaseUrl = "https://api.anthropic.com/v1/",
        defaultModels = listOf("claude-3-5-sonnet-20241022", "claude-3-haiku-20240307")
    ),
    GOOGLE(
        displayName = "Google Gemini",
        defaultBaseUrl = "https://generativelanguage.googleapis.com/v1beta/",
        defaultModels = listOf("gemini-1.5-pro-latest", "gemini-1.5-flash-latest")
    ),
    OLLAMA(
        displayName = "Ollama",
        defaultBaseUrl = "http://10.0.2.2:11434/",
        defaultModels = listOf("llama3.2", "qwen2.5", "phi4")
    ),
    CUSTOM(
        displayName = "Custom",
        defaultBaseUrl = "",
        defaultModels = emptyList()
    );

    companion object {
        fun fromProviderType(type: ProviderType): LLMProvider = when (type) {
            ProviderType.OPENAI -> OPENAI
            ProviderType.ANTHROPIC -> ANTHROPIC
            ProviderType.GOOGLE -> GOOGLE
            ProviderType.OLLAMA -> OLLAMA
            ProviderType.CUSTOM -> CUSTOM
        }
    }
}
