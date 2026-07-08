package com.jarvis.ai.ui.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jarvis.ai.ui.components.GlassCard
import com.jarvis.ai.ui.theme.JarvisColors
import com.jarvis.ai.util.getGreeting

@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(JarvisColors.DeepBlack, JarvisColors.DarkBg)
                )
            )
            .verticalScroll(scrollState)
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    color = JarvisColors.TextPrimary
                )
                Text(
                    text = getGreeting(navController.context),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JarvisColors.TextSecondary
                )
            }
            Icon(
                Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = JarvisColors.TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Weather Card
        DashboardCard(
            title = "Weather",
            icon = Icons.Default.WbSunny,
            iconColor = JarvisColors.Gold
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${uiState.temperature}°C",
                        style = MaterialTheme.typography.displaySmall,
                        color = JarvisColors.TextPrimary
                    )
                    Text(
                        text = uiState.weatherCondition,
                        style = MaterialTheme.typography.bodyMedium,
                        color = JarvisColors.TextSecondary
                    )
                }
                Icon(
                    Icons.Default.WbSunny,
                    contentDescription = null,
                    tint = JarvisColors.Gold,
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // System Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Battery
            DashboardMiniCard(
                title = "Battery",
                value = "${uiState.batteryLevel}%",
                icon = Icons.Default.BatteryFull,
                color = when {
                    uiState.batteryLevel < 20 -> JarvisColors.Error
                    uiState.batteryLevel < 50 -> JarvisColors.Warning
                    else -> JarvisColors.Success
                },
                modifier = Modifier.weight(1f)
            )

            // RAM
            DashboardMiniCard(
                title = "RAM",
                value = uiState.ramUsed,
                icon = Icons.Default.Memory,
                color = JarvisColors.BlueGlow,
                modifier = Modifier.weight(1f)
            )

            // Storage
            DashboardMiniCard(
                title = "Storage",
                value = uiState.storageUsed,
                icon = Icons.Default.Storage,
                color = JarvisColors.ArcReactor,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DashboardMiniCard(
                title = "CPU",
                value = uiState.cpuUsage,
                icon = Icons.Default.DeveloperBoard,
                color = JarvisColors.NeonBlue,
                modifier = Modifier.weight(1f)
            )

            DashboardMiniCard(
                title = "Network",
                value = if (uiState.isNetworkAvailable) "Connected" else "Offline",
                icon = if (uiState.isNetworkAvailable) Icons.Default.Wifi else Icons.Default.WifiOff,
                color = if (uiState.isNetworkAvailable) JarvisColors.Success else JarvisColors.Error,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Upcoming Events
        DashboardCard(
            title = "Upcoming Events",
            icon = Icons.Default.CalendarMonth,
            iconColor = JarvisColors.Red
        ) {
            if (uiState.upcomingEvents.isEmpty()) {
                Text(
                    text = "No upcoming events",
                    style = MaterialTheme.typography.bodyMedium,
                    color = JarvisColors.TextTertiary
                )
            } else {
                Column {
                    uiState.upcomingEvents.forEach { event ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Event,
                                contentDescription = null,
                                tint = JarvisColors.BlueGlow,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = event,
                                style = MaterialTheme.typography.bodyMedium,
                                color = JarvisColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reminders
        DashboardCard(
            title = "Reminders",
            icon = Icons.Default.Alarm,
            iconColor = JarvisColors.Gold
        ) {
            if (uiState.reminders.isEmpty()) {
                Text(
                    text = "No active reminders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = JarvisColors.TextTertiary
                )
            } else {
                Column {
                    uiState.reminders.forEach { reminder ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AlarmOn,
                                contentDescription = null,
                                tint = JarvisColors.Gold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = reminder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = JarvisColors.TextPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun DashboardCard(
    title: String,
    icon: ImageVector,
    iconColor: androidx.compose.ui.graphics.Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    color = JarvisColors.TextSecondary
                )
            }
            content()
        }
    }
}

@Composable
fun DashboardMiniCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = JarvisColors.TextPrimary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = JarvisColors.TextTertiary
            )
        }
    }
}
