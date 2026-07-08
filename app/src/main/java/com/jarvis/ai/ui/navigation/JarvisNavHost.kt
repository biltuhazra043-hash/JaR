package com.jarvis.ai.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.jarvis.ai.ui.home.HomeScreen
import com.jarvis.ai.ui.chat.ChatScreen
import com.jarvis.ai.ui.dashboard.DashboardScreen
import com.jarvis.ai.ui.settings.SettingsScreen
import com.jarvis.ai.ui.provider.AiProviderScreen
import com.jarvis.ai.ui.automation.AutomationScreen
import com.jarvis.ai.ui.memory.MemoryScreen
import com.jarvis.ai.ui.files.FilesScreen
import com.jarvis.ai.ui.security.SecurityScreen

@Composable
fun JarvisNavHost(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }
        composable(Screen.Chat.route) {
            ChatScreen(navController = navController)
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.AiProvider.route) {
            AiProviderScreen(navController = navController)
        }
        composable(Screen.Automation.route) {
            AutomationScreen(navController = navController)
        }
        composable(Screen.Memory.route) {
            MemoryScreen(navController = navController)
        }
        composable(Screen.Files.route) {
            FilesScreen(navController = navController)
        }
        composable(Screen.Security.route) {
            SecurityScreen(navController = navController)
        }
    }
}
