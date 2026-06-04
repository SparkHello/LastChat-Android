package com.lastchat.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "providers")
data class ProviderConfigEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: String,
    val apiKey: String,
    val baseUrl: String,
    val models: String,
    val isEnabled: Boolean,
    val isDefault: Boolean
)
