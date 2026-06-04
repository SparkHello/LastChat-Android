package com.lastchat.app.ui.screens.conversationlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastchat.app.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ConversationListViewModel(private val chatRepository: ChatRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _conversations = MutableStateFlow<List<com.lastchat.app.data.model.Conversation>>(emptyList())
    val conversations: StateFlow<List<com.lastchat.app.data.model.Conversation>> = _conversations.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                chatRepository.getAllConversations(),
                _searchQuery
            ) { allConversations, query ->
                if (query.isBlank()) {
                    allConversations
                } else {
                    allConversations.filter {
                        it.title.contains(query, ignoreCase = true)
                    }
                }
            }.collectLatest { filtered ->
                _conversations.value = filtered
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteConversation(id: String) {
        viewModelScope.launch {
            chatRepository.deleteConversationById(id)
        }
    }

    fun createNewChat(onNavigate: (String) -> Unit) {
        val newId = java.util.UUID.randomUUID().toString()
        viewModelScope.launch {
            val conversation = com.lastchat.app.data.model.Conversation(
                id = newId,
                title = "New Chat"
            )
            chatRepository.createConversation(conversation)
            onNavigate("chat?conversationId=$newId")
        }
    }
}
