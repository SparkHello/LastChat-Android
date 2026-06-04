package com.lastchat.app.ui.screens.assistant

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import com.lastchat.app.data.model.Assistant
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssistantEditScreen(
    assistantId: String?,
    onNavigateBack: () -> Unit,
    viewModel: AssistantViewModel = koinViewModel()
) {
    val selectedAssistant by viewModel.selectedAssistant.collectAsState()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var systemPrompt by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }

    val isEditing = !assistantId.isNullOrBlank()

    LaunchedEffect(assistantId) {
        viewModel.selectAssistant(assistantId)
    }

    LaunchedEffect(selectedAssistant) {
        selectedAssistant?.let {
            name = it.name
            description = it.description
            systemPrompt = it.systemPrompt
            tags = it.tags.joinToString(", ")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditing) "Edit Assistant" else "New Assistant") },
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
                    val assistant = if (isEditing && selectedAssistant != null) {
                        selectedAssistant!!.copy(
                            name = name,
                            description = description,
                            systemPrompt = systemPrompt,
                            tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        )
                    } else {
                        Assistant(
                            name = name,
                            description = description,
                            systemPrompt = systemPrompt,
                            tags = tags.split(",").map { it.trim() }.filter { it.isNotBlank() }
                        )
                    }
                    if (isEditing) {
                        viewModel.updateAssistant(assistant)
                    } else {
                        viewModel.createAssistant(assistant)
                    }
                    onNavigateBack()
                },
                
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

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = systemPrompt,
                onValueChange = { systemPrompt = it },
                label = { Text("System Prompt") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                supportingText = {
                    Text("Instructions that define how the AI assistant behaves")
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tags,
                onValueChange = { tags = it },
                label = { Text("Tags") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                supportingText = {
                    Text("Separate tags with commas (e.g., coding, writing)")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
