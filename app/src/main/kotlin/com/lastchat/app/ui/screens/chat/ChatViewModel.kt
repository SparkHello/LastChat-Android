package com.lastchat.app.ui.screens.chat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastchat.app.data.model.Conversation
import com.lastchat.app.data.model.Message
import com.lastchat.app.data.model.MessageRole
import com.lastchat.app.data.model.ProviderConfig
import com.lastchat.app.data.repository.ChatRepository
import com.lastchat.app.data.repository.ProviderRepository
import com.lastchat.app.service.LLMService
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(FlowPreview::class)
class ChatViewModel(
    private val chatRepository: ChatRepository,
    private val llmService: LLMService,
    private val providerRepository: ProviderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    init {
        val conversationId = savedStateHandle.get<String>("conversationId")
        if (conversationId != null) {
            loadConversation(conversationId)
        }
    }

    private fun loadConversation(conversationId: String) {
        viewModelScope.launch {
            val conversation = chatRepository.getConversationById(conversationId)
            if (conversation != null) {
                _uiState.update { it.copy(conversationId = conversationId, title = conversation.title) }
                chatRepository.getMessagesForConversation(conversationId).collect { msgs ->
                    _messages.value = msgs
                }
            }
        }
    }

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        val conversationId = _uiState.value.conversationId ?: UUID.randomUUID().toString()
        val isNewConversation = _uiState.value.conversationId == null

        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true, error = null) }

            try {
                if (isNewConversation) {
                    _uiState.update { it.copy(conversationId = conversationId) }
                    val conversation = Conversation(
                        id = conversationId,
                        title = text.take(50)
                    )
                    chatRepository.createConversation(conversation)

                    // Collect messages for new conversation
                    launch {
                        chatRepository.getMessagesForConversation(conversationId).collect { msgs ->
                            _messages.value = msgs
                        }
                    }
                }

                val userMessage = Message(
                    conversationId = conversationId,
                    role = MessageRole.USER,
                    content = text
                )
                chatRepository.sendMessage(userMessage)

                val allMessages = _messages.value.toList()

                val provider = providerRepository.getDefaultProvider()
                    ?: providerRepository.getAllProviders().first().firstOrNull { it.isEnabled }
                    ?: return@launch

                val model = provider.models.firstOrNull() ?: "gpt-4o-mini"

                val aiMessageId = UUID.randomUUID().toString()
                val assistantMessage = Message(
                    id = aiMessageId,
                    conversationId = conversationId,
                    role = MessageRole.ASSISTANT,
                    content = ""
                )
                chatRepository.sendMessage(assistantMessage)

                var fullResponse = ""
                llmService.sendMessageStream(
                    messages = allMessages + userMessage,
                    provider = provider,
                    model = model,
                    onToken = { token ->
                        fullResponse += token
                        _uiState.update { it.copy(currentResponse = fullResponse) }
                    },
                    onComplete = {
                        viewModelScope.launch {
                            val finalMessage = Message(
                                id = aiMessageId,
                                conversationId = conversationId,
                                role = MessageRole.ASSISTANT,
                                content = fullResponse
                            )
                            chatRepository.sendMessage(finalMessage)
                            _uiState.update { it.copy(isGenerating = false, inputText = "", currentResponse = "") }
                        }
                    },
                    onError = { error ->
                        _uiState.update {
                            it.copy(
                                isGenerating = false,
                                error = error.message ?: "An error occurred"
                            )
                        }
                    }
                )

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isGenerating = false,
                        error = e.message ?: "An error occurred"
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
