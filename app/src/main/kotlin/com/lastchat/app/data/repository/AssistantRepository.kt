package com.lastchat.app.data.repository

import com.lastchat.app.data.local.dao.AssistantDao
import com.lastchat.app.data.model.Assistant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AssistantRepository(private val assistantDao: AssistantDao) {

    fun getAllAssistants(): Flow<List<Assistant>> =
        assistantDao.getAll().map { list ->
            list.map { Assistant.fromEntity(it) }
        }

    suspend fun getAssistantById(id: String): Assistant? =
        assistantDao.getById(id)?.let { Assistant.fromEntity(it) }

    suspend fun createAssistant(assistant: Assistant) {
        assistantDao.insert(assistant.toEntity())
    }

    suspend fun updateAssistant(assistant: Assistant) {
        assistantDao.update(assistant.toEntity())
    }

    suspend fun deleteAssistant(assistant: Assistant) {
        assistantDao.delete(assistant.toEntity())
    }

    suspend fun deleteAssistantById(id: String) {
        assistantDao.deleteById(id)
    }

    fun searchAssistants(query: String): Flow<List<Assistant>> =
        assistantDao.search(query).map { list ->
            list.map { Assistant.fromEntity(it) }
        }

    suspend fun insertDefaultAssistants() {
        val defaults = listOf(
            Assistant(
                name = "General Assistant",
                description = "A helpful general-purpose AI assistant",
                systemPrompt = "You are a helpful, harmless, and honest assistant. Answer questions to the best of your ability.",
                tags = listOf("general", "default")
            ),
            Assistant(
                name = "Code Expert",
                description = "Specialized in programming and code review",
                systemPrompt = "You are an expert programmer. Help users write, debug, and understand code. Provide clear explanations and best practices. Always use markdown code blocks for code.",
                tags = listOf("code", "programming")
            ),
            Assistant(
                name = "Writing Assistant",
                description = "Helps with writing, editing, and proofreading",
                systemPrompt = "You are a skilled writing assistant. Help users improve their writing, suggest edits, brainstorm ideas, and provide feedback on style, grammar, and clarity.",
                tags = listOf("writing", "editing")
            ),
            Assistant(
                name = "Translator",
                description = "Expert in multiple languages",
                systemPrompt = "You are a professional translator. Translate text accurately while preserving meaning, tone, and context. Explain cultural nuances when relevant.",
                tags = listOf("translation", "languages")
            )
        )
        defaults.forEach { assistantDao.insert(it.toEntity()) }
    }
}
