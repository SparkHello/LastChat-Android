package com.lastchat.app.ui.screens.assistant

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastchat.app.data.model.Assistant
import com.lastchat.app.data.repository.AssistantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AssistantViewModel(private val assistantRepository: AssistantRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _assistants = MutableStateFlow<List<Assistant>>(emptyList())
    val assistants: StateFlow<List<Assistant>> = _assistants.asStateFlow()

    private val _selectedAssistant = MutableStateFlow<Assistant?>(null)
    val selectedAssistant: StateFlow<Assistant?> = _selectedAssistant.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                assistantRepository.getAllAssistants(),
                _searchQuery
            ) { all, query ->
                if (query.isBlank()) all
                else all.filter {
                    it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true) ||
                    it.tags.any { tag -> tag.contains(query, ignoreCase = true) }
                }
            }.collectLatest { filtered ->
                _assistants.value = filtered
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectAssistant(id: String?) {
        viewModelScope.launch {
            _selectedAssistant.value = id?.let { assistantRepository.getAssistantById(it) }
        }
    }

    fun createAssistant(assistant: Assistant) {
        viewModelScope.launch {
            assistantRepository.createAssistant(assistant)
            _selectedAssistant.value = null
        }
    }

    fun updateAssistant(assistant: Assistant) {
        viewModelScope.launch {
            assistantRepository.updateAssistant(assistant)
            _selectedAssistant.value = null
        }
    }

    fun deleteAssistant(id: String) {
        viewModelScope.launch {
            assistantRepository.deleteAssistantById(id)
            if (_selectedAssistant.value?.id == id) {
                _selectedAssistant.value = null
            }
        }
    }
}
