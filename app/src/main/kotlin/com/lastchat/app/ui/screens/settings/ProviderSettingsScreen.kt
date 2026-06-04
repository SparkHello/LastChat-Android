package com.lastchat.app.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lastchat.app.data.model.LLMProvider
import com.lastchat.app.data.model.ProviderConfig
import com.lastchat.app.data.model.ProviderType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderSettingsScreen(
    providerId: String?,
    onNavigateBack: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val providers by viewModel.providers.collectAsState()
    val existingProvider = providers.find { it.id == providerId }

    val isEditing = !providerId.isNullOrBlank()

    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ProviderType.OPENAI) }
    var apiKey by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("") }
    var modelsText by remember { mutableStateOf("") }
    var isEnabled by remember { mutableStateOf(true) }
    var isDefault by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(existingProvider) {
        existingProvider?.let { p ->
            name = p.name
            selectedType = p.type
            apiKey = p.apiKey
            baseUrl = p.baseUrl
            modelsText = p.models.joinToString("\n")
            isEnabled = p.isEnabled
            isDefault = p.isDefault
        } ?: run {
            // Set defaults for new provider
            val llmProvider = LLMProvider.OPENAI
            name = llmProvider.displayName
            baseUrl = llmProvider.defaultBaseUrl
            modelsText = llmProvider.defaultModels.joinToString("\n")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Provider" else "Add Provider") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val models = modelsText.lines().map { it.trim() }.filter { it.isNotBlank() }
                    val provider = if (isEditing && existingProvider != null) {
                        existingProvider.copy(
                            name = name,
                            type = selectedType,
                            apiKey = apiKey,
                            baseUrl = baseUrl,
                            models = models,
                            isEnabled = isEnabled,
                            isDefault = isDefault
                        )
                    } else {
                        ProviderConfig(
                            name = name,
                            type = selectedType,
                            apiKey = apiKey,
                            baseUrl = baseUrl,
                            models = models,
                            isEnabled = isEnabled,
                            isDefault = isDefault
                        )
                    }
                    if (isEditing) {
                        viewModel.updateProvider(provider)
                    } else {
                        viewModel.createProvider(provider)
                    }
                    if (isDefault) {
                        viewModel.setDefaultProvider(provider.id)
                    }
                    onNavigateBack()
                },
                enabled = name.isNotBlank() && baseUrl.isNotBlank()
            ) {
                Icon(Icons.Default.Save, contentDescription = "Save")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Provider Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeDropdownExpanded,
                onExpandedChange = { typeDropdownExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Provider Type") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = typeDropdownExpanded,
                    onDismissRequest = { typeDropdownExpanded = false }
                ) {
                    ProviderType.entries.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedType = type
                                val llmProvider = LLMProvider.fromProviderType(type)
                                if (!isEditing) {
                                    name = llmProvider.displayName
                                    baseUrl = llmProvider.defaultBaseUrl
                                    modelsText = llmProvider.defaultModels.joinToString("\n")
                                }
                                typeDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("API Key") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = baseUrl,
                onValueChange = { baseUrl = it },
                label = { Text("Base URL *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = modelsText,
                onValueChange = { modelsText = it },
                label = { Text("Models") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6,
                supportingText = { Text("Enter one model ID per line") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ListItem(
                headlineContent = { Text("Enabled") },
                trailingContent = {
                    Switch(checked = isEnabled, onCheckedChange = { isEnabled = it })
                }
            )

            ListItem(
                headlineContent = { Text("Set as Default") },
                trailingContent = {
                    Switch(checked = isDefault, onCheckedChange = { isDefault = it })
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
