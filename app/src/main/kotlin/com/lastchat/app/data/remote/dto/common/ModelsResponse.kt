package com.lastchat.app.data.remote.dto.common

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ModelsResponse(
    val data: List<ModelDto> = emptyList()
)

@Serializable
data class ModelDto(
    val id: String,
    @SerialName("object")
    val objectType: String? = null,
    @SerialName("owned_by")
    val ownedBy: String? = null
)
