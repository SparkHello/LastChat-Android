package com.lastchat.app.data.model

import com.lastchat.app.data.local.entity.AssistantEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

data class Assistant(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val systemPrompt: String = "You are a helpful assistant.",
    val tags: List<String> = emptyList(),
    val avatarUrl: String? = null,
    val defaultProviderId: String? = null,
    val defaultModelId: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): AssistantEntity = AssistantEntity(
        id = id,
        name = name,
        description = description,
        systemPrompt = systemPrompt,
        tags = Json.encodeToString(tags),
        avatarUrl = avatarUrl,
        defaultProviderId = defaultProviderId,
        defaultModelId = defaultModelId,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(entity: AssistantEntity): Assistant = Assistant(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            systemPrompt = entity.systemPrompt,
            tags = Json.decodeFromString(entity.tags),
            avatarUrl = entity.avatarUrl,
            defaultProviderId = entity.defaultProviderId,
            defaultModelId = entity.defaultModelId,
            createdAt = entity.createdAt
        )
    }
}
