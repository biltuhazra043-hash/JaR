package com.jarvis.ai.ui.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jarvis.ai.ui.theme.JarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityScreen(navController: NavController) {
    var biometricEnabled by remember { mutableStateOf(false) }
    var pinEnabled by remember { mutableStateOf(false) }

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
            Text("Security", style = MaterialTheme.typography.titleMedium, color = JarvisColors.TextPrimary)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Status icon
            Box(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = JarvisColors.BlueGlow,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Security Settings", style = MaterialTheme.typography.headlineSmall, color = JarvisColors.TextPrimary)
                    Text(
                        "Protect your data and Jarvis",
                        style = MaterialTheme.typography.bodyMedium,
                        color = JarvisColors.TextTertiary
                    )
                }
            }

            // Biometric Lock
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Fingerprint, null, tint = JarvisColors.BlueGlow, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Biometric Lock", style = MaterialTheme.typography.bodyLarge, color = JarvisColors.TextPrimary)
                        Text("Unlock with fingerprint or face", style = MaterialTheme.typography.bodySmall, color = JarvisColors.TextTertiary)
                    }
                    Switch(
                        checked = biometricEnabled,
                        onCheckedChange = { biometricEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JarvisColors.BlueGlow,
                            checkedTrackColor = JarvisColors.BlueGlow.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // PIN Lock
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Pin, null, tint = JarvisColors.Red, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("PIN Lock", style = MaterialTheme.typography.bodyLarge, color = JarvisColors.TextPrimary)
                        Text("Require PIN to access Jarvis", style = MaterialTheme.typography.bodySmall, color = JarvisColors.TextTertiary)
                    }
                    Switch(
                        checked = pinEnabled,
                        onCheckedChange = { pinEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = JarvisColors.BlueGlow,
                            checkedTrackColor = JarvisColors.BlueGlow.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            // Encryption status
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = JarvisColors.Success.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, null, tint = JarvisColors.Success, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Encryption Active", style = MaterialTheme.typography.bodyLarge, color = JarvisColors.Success)
                        Text("API keys and data are encrypted", style = MaterialTheme.typography.bodySmall, color = JarvisColors.TextTertiary)
                    }
                }
            }
        }
    }
}
