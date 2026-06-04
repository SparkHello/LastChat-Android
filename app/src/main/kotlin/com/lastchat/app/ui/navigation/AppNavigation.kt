package com.lastchat.app.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.lastchat.app.ui.screens.assistant.AssistantEditScreen
import com.lastchat.app.ui.screens.assistant.AssistantListScreen
import com.lastchat.app.ui.screens.chat.ChatScreen
import com.lastchat.app.ui.screens.conversationlist.ConversationListScreen
import com.lastchat.app.ui.screens.settings.ProviderSettingsScreen
import com.lastchat.app.ui.screens.settings.SettingsScreen

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Chat : Screen("chat", "Chat", Icons.AutoMirrored.Filled.Chat)
    object History : Screen("history", "History", Icons.Default.History)
    object Assistants : Screen("assistants", "Assistants", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object AssistantEdit : Screen("assistant/edit/{assistantId}", "Edit", Icons.Default.Person) {
        fun createRoute(assistantId: String = "") = "assistant/edit/$assistantId"
    }
    object ProviderEdit : Screen("settings/provider/{providerId}", "Provider", Icons.Default.Settings) {
        fun createRoute(providerId: String = "") = "settings/provider/$providerId"
    }
}

val bottomNavItems = listOf(Screen.Chat, Screen.History, Screen.Assistants, Screen.Settings)

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomNavItems.forEach { screen ->
            val selected = when (screen.route) {
                "chat" -> currentRoute?.startsWith("chat") == true
                else -> currentRoute == screen.route
            }
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.label) },
                label = { Text(screen.label) },
                selected = selected,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController, innerPadding: PaddingValues) {
    NavHost(
        navController = navController,
        startDestination = Screen.Chat.route,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(
            route = "chat?conversationId={conversationId}",
            arguments = listOf(
                navArgument("conversationId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId")
            ChatScreen(
                conversationId = conversationId,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.History.route) {
            ConversationListScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(Screen.Assistants.route) {
            AssistantListScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = Screen.AssistantEdit.route,
            arguments = listOf(
                navArgument("assistantId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val assistantId = backStackEntry.arguments?.getString("assistantId")
            AssistantEditScreen(
                assistantId = assistantId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = Screen.ProviderEdit.route,
            arguments = listOf(
                navArgument("providerId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getString("providerId")
            ProviderSettingsScreen(
                providerId = providerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
