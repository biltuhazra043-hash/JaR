package com.jarvis.ai.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Home : Screen("home", "Home", Icons.Default.Home)
    data object Chat : Screen("chat", "Chat", Icons.Default.Chat)
    data object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    data object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    data object AiProvider : Screen("ai_provider", "AI Provider", Icons.Default.SmartToy)
    data object Automation : Screen("automation", "Automation", Icons.Default.AutoAwesome)
    data object Memory : Screen("memory", "Memory", Icons.Default.Memory)
    data object Files : Screen("files", "Files", Icons.Default.Folder)
    data object Security : Screen("security", "Security", Icons.Default.Lock)
    data object Telegram : Screen("telegram", "Telegram", Icons.Default.Send)
    data object SystemPrompt : Screen("system_prompt", "System Prompt", Icons.Default.EditNote)

    companion object {
        val bottomNavItems = listOf(Home, Chat, Dashboard, Settings)
    }
}
