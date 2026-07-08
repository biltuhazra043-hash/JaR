package com.jarvis.ai.ui.automation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jarvis.ai.ui.theme.JarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutomationScreen(
    navController: NavController,
    viewModel: AutomationViewModel = hiltViewModel()
) {
    val automations by viewModel.automations.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(JarvisColors.DeepBlack, JarvisColors.DarkBg)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JarvisColors.GunmetalDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = JarvisColors.TextPrimary)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Automation", style = MaterialTheme.typography.titleMedium, color = JarvisColors.TextPrimary)
        }

        LazyColumn(
            modifier = Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Preset routines
            item {
                Text("Quick Routines", style = MaterialTheme.typography.labelLarge, color = JarvisColors.BlueGlow)
            }

            item {
                PresetRoutineCard(
                    icon = Icons.Default.WbSunny,
                    title = "Morning Routine",
                    subtitle = "Weather, news, calendar, reminders",
                    color = JarvisColors.Gold,
                    onActivate = { viewModel.activatePreset("morning") }
                )
            }

            item {
                PresetRoutineCard(
                    icon = Icons.Default.NightsStay,
                    title = "Night Routine",
                    subtitle = "DND, dim lights, tomorrow's summary",
                    color = JarvisColors.ArcReactor,
                    onActivate = { viewModel.activatePreset("night") }
                )
            }

            item {
                PresetRoutineCard(
                    icon = Icons.Default.BatteryChargingFull,
                    title = "Charging Routine",
                    subtitle = "Enable WiFi sync, backup, updates",
                    color = JarvisColors.Success,
                    onActivate = { viewModel.activatePreset("charging") }
                )
            }

            if (automations.isNotEmpty()) {
                item {
                    Text("Custom Automations", style = MaterialTheme.typography.labelLarge, color = JarvisColors.BlueGlow)
                }
                items(automations) { automation ->
                    AutomationCard(
                        name = automation.name,
                        type = automation.type,
                        isEnabled = automation.isEnabled,
                        onToggle = { viewModel.toggleAutomation(automation.id) },
                        onDelete = { viewModel.deleteAutomation(automation.id) }
                    )
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = JarvisColors.BlueGlow,
            contentColor = JarvisColors.DeepBlack,
            modifier = Modifier.padding(16.dp).align(androidx.compose.ui.Alignment.End)
        ) {
            Icon(Icons.Default.Add, "Add Automation")
        }
    }
}

@Composable
fun PresetRoutineCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onActivate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onActivate() },
        colors = CardDefaults.cardColors(containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = color.copy(alpha = 0.1f),
                modifier = Modifier.size(40.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = color)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = JarvisColors.TextPrimary)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = JarvisColors.TextTertiary)
            }
            Icon(Icons.Default.PlayArrow, "Activate", tint = color)
        }
    }
}

@Composable
fun AutomationCard(
    name: String,
    type: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(name, style = MaterialTheme.typography.bodyLarge, color = JarvisColors.TextPrimary)
                Text(type, style = MaterialTheme.typography.bodySmall, color = JarvisColors.TextTertiary)
            }
            Switch(
                checked = isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = JarvisColors.BlueGlow,
                    checkedTrackColor = JarvisColors.BlueGlow.copy(alpha = 0.3f)
                )
            )
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = JarvisColors.Error)
            }
        }
    }
}
