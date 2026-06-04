package com.lastchat.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lastchat.app.data.model.ProviderConfig
import com.lastchat.app.data.repository.ProviderRepository
import com.lastchat.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _providers = MutableStateFlow<List<ProviderConfig>>(emptyList())
    val providers: StateFlow<List<ProviderConfig>> = _providers.asStateFlow()

    private val _dynamicColor = MutableStateFlow(true)
    val dynamicColor: StateFlow<Boolean> = _dynamicColor.asStateFlow()

    private val _darkMode = MutableStateFlow("system")
    val darkMode: StateFlow<String> = _darkMode.asStateFlow()

    private val _streamingEnabled = MutableStateFlow(true)
    val streamingEnabled: StateFlow<Boolean> = _streamingEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            providerRepository.getAllProviders().collectLatest {
                _providers.value = it
            }
        }
        viewModelScope.launch {
            settingsRepository.dynamicColor.collectLatest { _dynamicColor.value = it }
        }
        viewModelScope.launch {
            settingsRepository.darkMode.collectLatest { _darkMode.value = it }
        }
        viewModelScope.launch {
            settingsRepository.streamingEnabled.collectLatest { _streamingEnabled.value = it }
        }
    }

    fun setDynamicColor(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setDynamicColor(enabled) }
    }

    fun setDarkMode(mode: String) {
        viewModelScope.launch { settingsRepository.setDarkMode(mode) }
    }

    fun setStreamingEnabled(enabled: Boolean) {
        viewModelScope.launch { settingsRepository.setStreamingEnabled(enabled) }
    }

    fun createProvider(provider: ProviderConfig) {
        viewModelScope.launch { providerRepository.createProvider(provider) }
    }

    fun updateProvider(provider: ProviderConfig) {
        viewModelScope.launch { providerRepository.updateProvider(provider) }
    }

    fun deleteProvider(id: String) {
        viewModelScope.launch { providerRepository.deleteProviderById(id) }
    }

    fun setDefaultProvider(id: String) {
        viewModelScope.launch { providerRepository.setDefaultProvider(id) }
    }
}
