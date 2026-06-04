package com.lastchat.app.data.model

import com.lastchat.app.data.local.entity.ConversationEntity
import java.util.UUID

data class Conversation(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "New Chat",
    val assistantId: String? = null,
    val providerId: String? = null,
    val modelId: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val messageCount: Int = 0
) {
    fun toEntity(): ConversationEntity = ConversationEntity(
        id = id,
        title = title,
        assistantId = assistantId,
        providerId = providerId,
        modelId = modelId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        messageCount = messageCount
    )

    companion object {
        fun fromEntity(entity: ConversationEntity): Conversation = Conversation(
            id = entity.id,
            title = entity.title,
            assistantId = entity.assistantId,
            providerId = entity.providerId,
            modelId = entity.modelId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            messageCount = entity.messageCount
        )
    }
}
