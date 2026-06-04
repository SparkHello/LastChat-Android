package com.lastchat.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assistants")
data class AssistantEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val systemPrompt: String,
    val tags: String,
    val avatarUrl: String?,
    val defaultProviderId: String?,
    val defaultModelId: String?,
    val createdAt: Long
)
