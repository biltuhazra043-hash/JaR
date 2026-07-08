package com.jarvis.ai.ui.home

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jarvis.ai.ui.components.*
import com.jarvis.ai.ui.navigation.Screen
import com.jarvis.ai.ui.theme.JarvisColors

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        JarvisColors.DeepBlack,
                        JarvisColors.DarkBg,
                        JarvisColors.DeepBlack
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Greeting
            Text(
                text = uiState.greeting,
                style = MaterialTheme.typography.headlineMedium,
                color = JarvisColors.TextPrimary,
                textAlign = TextAlign.Center
            )

            Text(
                text = uiState.statusText,
                style = MaterialTheme.typography.bodyMedium,
                color = JarvisColors.BlueGlow,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Iron Man Mask / Arc Reactor
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(220.dp)
            ) {
                ArcReactorAnimation(
                    size = 180.dp,
                    isActive = uiState.isListening || uiState.isSpeaking
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Voice Waveform
            VoiceWaveform(
                isActive = uiState.isListening || uiState.isSpeaking,
                barCount = 24,
                modifier = Modifier.fillMaxWidth(0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                StatusDot(
                    isActive = uiState.isListening,
                    activeColor = JarvisColors.BlueGlow,
                    size = 10.dp
                )
                Text(
                    text = when {
                        uiState.isListening -> "Listening..."
                        uiState.isSpeaking -> "Speaking..."
                        uiState.isProcessing -> "Processing..."
                        else -> "Standing by"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = JarvisColors.TextSecondary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Microphone button
                FilledIconButton(
                    onClick = { viewModel.toggleListening() },
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = if (uiState.isListening) JarvisColors.Red else JarvisColors.BlueGlow.copy(alpha = 0.2f),
                        contentColor = if (uiState.isListening) Color.White else JarvisColors.BlueGlow
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (uiState.isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = "Toggle voice",
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Chat button
                FilledIconButton(
                    onClick = { navController.navigate(Screen.Chat.route) },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = JarvisColors.Gunmetal.copy(alpha = 0.8f),
                        contentColor = JarvisColors.TextSecondary
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubble,
                        contentDescription = "Chat",
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Settings button
                FilledIconButton(
                    onClick = { navController.navigate(Screen.Settings.route) },
                    modifier = Modifier.size(56.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = JarvisColors.Gunmetal.copy(alpha = 0.8f),
                        contentColor = JarvisColors.TextSecondary
                    ),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Quick actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(JarvisColors.GlassSurface)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionItem(Icons.Default.WbSunny, "Weather", onClick = {})
                QuickActionItem(Icons.Default.BatteryFull, "Battery", onClick = {})
                QuickActionItem(Icons.Default.Timer, "Timer", onClick = {})
                QuickActionItem(Icons.Default.Search, "Search", onClick = {})
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuickActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = JarvisColors.BlueGlow,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = JarvisColors.TextTertiary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
