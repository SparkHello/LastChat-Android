package com.lastchat.app.ui.screens.chat

data class ChatUiState(
    val conversationId: String? = null,
    val title: String = "New Chat",
    val isLoading: Boolean = false,
    val isGenerating: Boolean = false,
    val error: String? = null,
    val inputText: String = "",
    val currentResponse: String = ""
)
