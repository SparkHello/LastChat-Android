package com.lastchat.app.data.repository

import com.lastchat.app.data.local.dao.ProviderConfigDao
import com.lastchat.app.data.model.LLMProvider
import com.lastchat.app.data.model.ProviderConfig
import com.lastchat.app.data.model.ProviderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProviderRepository(private val providerConfigDao: ProviderConfigDao) {

    fun getAllProviders(): Flow<List<ProviderConfig>> =
        providerConfigDao.getAll().map { list ->
            list.map { ProviderConfig.fromEntity(it) }
        }

    suspend fun getProviderById(id: String): ProviderConfig? =
        providerConfigDao.getById(id)?.let { ProviderConfig.fromEntity(it) }

    suspend fun getDefaultProvider(): ProviderConfig? =
        providerConfigDao.getDefault()?.let { ProviderConfig.fromEntity(it) }

    suspend fun createProvider(provider: ProviderConfig) {
        providerConfigDao.insert(provider.toEntity())
    }

    suspend fun updateProvider(provider: ProviderConfig) {
        providerConfigDao.update(provider.toEntity())
    }

    suspend fun deleteProvider(provider: ProviderConfig) {
        providerConfigDao.delete(provider.toEntity())
    }

    suspend fun deleteProviderById(id: String) {
        providerConfigDao.deleteById(id)
    }

    suspend fun setDefaultProvider(id: String) {
        providerConfigDao.clearDefault()
        providerConfigDao.setDefault(id)
    }

    suspend fun insertDefaultProviders() {
        val defaults = listOf(
            ProviderConfig(
                name = LLMProvider.OPENAI.displayName,
                type = ProviderType.OPENAI,
                baseUrl = LLMProvider.OPENAI.defaultBaseUrl,
                models = LLMProvider.OPENAI.defaultModels,
                isDefault = true
            ),
            ProviderConfig(
                name = LLMProvider.ANTHROPIC.displayName,
                type = ProviderType.ANTHROPIC,
                baseUrl = LLMProvider.ANTHROPIC.defaultBaseUrl,
                models = LLMProvider.ANTHROPIC.defaultModels
            ),
            ProviderConfig(
                name = LLMProvider.GOOGLE.displayName,
                type = ProviderType.GOOGLE,
                baseUrl = LLMProvider.GOOGLE.defaultBaseUrl,
                models = LLMProvider.GOOGLE.defaultModels
            ),
            ProviderConfig(
                name = LLMProvider.OLLAMA.displayName,
                type = ProviderType.OLLAMA,
                baseUrl = LLMProvider.OLLAMA.defaultBaseUrl,
                models = LLMProvider.OLLAMA.defaultModels
            )
        )
        defaults.forEach { providerConfigDao.insert(it.toEntity()) }
    }
}
