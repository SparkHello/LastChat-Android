package com.lastchat.app.data.model

import com.lastchat.app.data.local.entity.ProviderConfigEntity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

enum class ProviderType {
    OPENAI, ANTHROPIC, GOOGLE, OLLAMA, CUSTOM
}

data class ProviderConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val type: ProviderType,
    val apiKey: String = "",
    val baseUrl: String,
    val models: List<String> = emptyList(),
    val isEnabled: Boolean = true,
    val isDefault: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toEntity(): ProviderConfigEntity = ProviderConfigEntity(
        id = id,
        name = name,
        type = type.name,
        apiKey = apiKey,
        baseUrl = baseUrl,
        models = Json.encodeToString(models),
        isEnabled = isEnabled,
        isDefault = isDefault,
        createdAt = createdAt
    )

    companion object {
        fun fromEntity(entity: ProviderConfigEntity): ProviderConfig = ProviderConfig(
            id = entity.id,
            name = entity.name,
            type = ProviderType.valueOf(entity.type),
            apiKey = entity.apiKey,
            baseUrl = entity.baseUrl,
            models = Json.decodeFromString(entity.models),
            isEnabled = entity.isEnabled,
            isDefault = entity.isDefault,
            createdAt = entity.createdAt
        )
    }
}
