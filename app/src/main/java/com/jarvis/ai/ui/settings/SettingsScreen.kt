package com.jarvis.ai.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jarvis.ai.ui.navigation.Screen
import com.jarvis.ai.ui.theme.JarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(JarvisColors.DeepBlack, JarvisColors.DarkBg)
                )
            )
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineSmall,
            color = JarvisColors.TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        // AI Provider section
        SectionHeader("AI Configuration")
        SettingsItem(
            icon = Icons.Default.SmartToy,
            title = "AI Provider",
            subtitle = "OpenRouter, Ollama, Custom APIs",
            iconColor = JarvisColors.BlueGlow,
            onClick = { navController.navigate(Screen.AiProvider.route) }
        )
        SettingsItem(
            icon = Icons.Default.EditNote,
            title = "System Prompt",
            subtitle = "Customize Jarvis personality",
            iconColor = JarvisColors.ArcReactor,
            onClick = { navController.navigate(Screen.SystemPrompt.route) }
        )

        // Voice section
        SectionHeader("Voice & Language")
        SettingsItem(
            icon = Icons.Default.RecordVoiceOver,
            title = "Voice Settings",
            subtitle = "Speed, pitch, voice selection",
            iconColor = JarvisColors.BlueGlow,
            onClick = {}
        )
        SettingsItem(
            icon = Icons.Default.Language,
            title = "Language",
            subtitle = "English",
            iconColor = JarvisColors.ArcReactor,
            onClick = {}
        )
        SettingsItem(
            icon = Icons.Default.Mic,
            title = "Wake Word",
            subtitle = "\"Hey Jarvis\" - Enabled",
            iconColor = JarvisColors.Success,
            onClick = {}
        )

        // Automation section
        SectionHeader("Automation")
        SettingsItem(
            icon = Icons.Default.AutoAwesome,
            title = "Routines",
            subtitle = "Morning, Night, Custom routines",
            iconColor = JarvisColors.Gold,
            onClick = { navController.navigate(Screen.Automation.route) }
        )
        SettingsItem(
            icon = Icons.Default.Memory,
            title = "Memory",
            subtitle = "Persistent memory & preferences",
            iconColor = JarvisColors.BlueGlow,
            onClick = { navController.navigate(Screen.Memory.route) }
        )

        // Integration section
        SectionHeader("Integrations")
        SettingsItem(
            icon = Icons.Default.Send,
            title = "Telegram Bot",
            subtitle = "Remote control & notifications",
            iconColor = JarvisColors.BlueAccent,
            onClick = { navController.navigate(Screen.Telegram.route) }
        )
        SettingsItem(
            icon = Icons.Default.Notifications,
            title = "Smart Notifications",
            subtitle = "AI-powered notification filtering",
            iconColor = JarvisColors.Warning,
            onClick = {}
        )

        // Security section
        SectionHeader("Security")
        SettingsItem(
            icon = Icons.Default.Lock,
            title = "Biometric Lock",
            subtitle = "Fingerprint / Face unlock",
            iconColor = JarvisColors.Red,
            onClick = { navController.navigate(Screen.Security.route) }
        )
        SettingsItem(
            icon = Icons.Default.Pin,
            title = "PIN Lock",
            subtitle = "Additional security layer",
            iconColor = JarvisColors.Red,
            onClick = { navController.navigate(Screen.Security.route) }
        )

        // Appearance section
        SectionHeader("Appearance")
        SettingsItem(
            icon = Icons.Default.Palette,
            title = "Theme",
            subtitle = "Dark (Iron Man)",
            iconColor = JarvisColors.BlueGlow,
            onClick = {}
        )
        SettingsItem(
            icon = Icons.Default.Animation,
            title = "Animation Quality",
            subtitle = "High",
            iconColor = JarvisColors.ArcReactor,
            onClick = {}
        )

        // Files section
        SectionHeader("Files & Data")
        SettingsItem(
            icon = Icons.Default.Folder,
            title = "File Manager",
            subtitle = "Browse and manage files",
            iconColor = JarvisColors.Gold,
            onClick = { navController.navigate(Screen.Files.route) }
        )
        SettingsItem(
            icon = Icons.Default.Backup,
            title = "Backup & Restore",
            subtitle = "Export settings and data",
            iconColor = JarvisColors.Success,
            onClick = {}
        )

        // About section
        SectionHeader("About")
        SettingsItem(
            icon = Icons.Default.Info,
            title = "About Jarvis",
            subtitle = "Version 1.0.0",
            iconColor = JarvisColors.TextSecondary,
            onClick = {}
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = JarvisColors.BlueGlow,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = iconColor.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = JarvisColors.TextPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = JarvisColors.TextTertiary
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = JarvisColors.TextTertiary,
            modifier = Modifier.size(20.dp)
        )
    }
}
