package com.lastchat.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lastchat.app.data.repository.AssistantRepository
import com.lastchat.app.data.repository.ProviderRepository
import com.lastchat.app.ui.navigation.AppNavigation
import com.lastchat.app.ui.navigation.BottomNavigationBar
import com.lastchat.app.ui.theme.LastChatTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val assistantRepository: AssistantRepository by inject()
    private val providerRepository: ProviderRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Insert default data on first launch
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val assistants = assistantRepository.getAllAssistants()
                val assistantsList = mutableListOf<com.lastchat.app.data.model.Assistant>()
                assistants.collect { list ->
                    assistantsList.addAll(list)
                }
                if (assistantsList.isEmpty()) {
                    assistantRepository.insertDefaultAssistants()
                }

                val providers = providerRepository.getAllProviders()
                val providersList = mutableListOf<com.lastchat.app.data.model.ProviderConfig>()
                providers.collect { list ->
                    providersList.addAll(list)
                }
                if (providersList.isEmpty()) {
                    providerRepository.insertDefaultProviders()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        enableEdgeToEdge()
        setContent {
            LastChatTheme {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = currentRoute in listOf("chat", "chat?conversationId={conversationId}", "history", "assistants", "settings") ||
                    currentRoute?.startsWith("chat") == true

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController)
                        }
                    }
                ) { innerPadding ->
                    AppNavigation(
                        navController = navController,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}
