package com.lastchat.app.data.model

import com.lastchat.app.data.local.entity.MessageEntity
import java.util.UUID

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val conversationId: String,
    val role: MessageRole,
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): MessageEntity = MessageEntity(
        id = id,
        conversationId = conversationId,
        role = role.name,
        content = content,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(entity: MessageEntity): Message = Message(
            id = entity.id,
            conversationId = entity.conversationId,
            role = MessageRole.valueOf(entity.role),
            content = entity.content,
            createdAt = entity.createdAt
        )
    }
}
