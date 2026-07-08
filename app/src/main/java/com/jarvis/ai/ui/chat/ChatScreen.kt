package com.jarvis.ai.ui.chat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jarvis.ai.domain.model.MessageRole
import com.jarvis.ai.ui.theme.JarvisColors
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messages by viewModel.messages.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Auto-scroll to bottom
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        JarvisColors.DeepBlack,
                        JarvisColors.DarkBg
                    )
                )
            )
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JarvisColors.GunmetalDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = JarvisColors.TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "Jarvis",
                    style = MaterialTheme.typography.titleMedium,
                    color = JarvisColors.TextPrimary
                )
                Text(
                    text = if (isProcessing) "Thinking..." else "Online",
                    style = MaterialTheme.typography.bodySmall,
                    color = JarvisColors.BlueGlow
                )
            }
        }

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = JarvisColors.BlueGlow.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "How can I assist you, Sir?",
                                style = MaterialTheme.typography.bodyLarge,
                                color = JarvisColors.TextSecondary
                            )
                        }
                    }
                }
            }

            items(messages) { message ->
                ChatMessageBubble(
                    content = message.content,
                    isUser = message.role == MessageRole.USER,
                    isStreaming = message.isStreaming
                )
            }

            if (isProcessing) {
                item {
                    TypingIndicator()
                }
            }
        }

        // Input area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(JarvisColors.GunmetalDark)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { viewModel.updateInputText(it) },
                modifier = Modifier.weight(1f),
                placeholder = {
                    Text(
                        "Speak or type a command...",
                        color = JarvisColors.TextTertiary
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = JarvisColors.TextPrimary,
                    unfocusedTextColor = JarvisColors.TextPrimary,
                    focusedBorderColor = JarvisColors.BlueGlow,
                    unfocusedBorderColor = JarvisColors.GunmetalLight,
                    cursorColor = JarvisColors.BlueGlow,
                    focusedContainerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f),
                    unfocusedContainerColor = JarvisColors.Gunmetal.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = { viewModel.startVoiceInput() }
            ) {
                Icon(
                    Icons.Default.Mic,
                    contentDescription = "Voice input",
                    tint = JarvisColors.BlueGlow
                )
            }

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage()
                    }
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank()) JarvisColors.BlueGlow else JarvisColors.TextTertiary
                )
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    content: String,
    isUser: Boolean,
    isStreaming: Boolean = false
) {
    val alignment = if (isUser) Arrangement.End else Arrangement.Start
    val bubbleColor = if (isUser) JarvisColors.BlueGlow.copy(alpha = 0.2f) else JarvisColors.Gunmetal.copy(alpha = 0.6f)
    val borderColor = if (isUser) JarvisColors.BlueGlow.copy(alpha = 0.3f) else JarvisColors.GlassBorder

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = alignment
    ) {
        if (!isUser) {
            Icon(
                Icons.Default.SmartToy,
                contentDescription = null,
                tint = JarvisColors.BlueGlow,
                modifier = Modifier
                    .size(32.dp)
                    .padding(end = 8.dp)
                    .clip(CircleShape)
                    .background(JarvisColors.Gunmetal)
                    .padding(6.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = bubbleColor,
            tonalElevation = 0.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = JarvisColors.TextPrimary,
                modifier = Modifier.padding(12.dp),
                textAlign = if (isUser) TextAlign.End else TextAlign.Start
            )
        }

        if (isUser) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = JarvisColors.TextSecondary,
                modifier = Modifier
                    .size(32.dp)
                    .padding(start = 8.dp)
                    .clip(CircleShape)
                    .background(JarvisColors.Gunmetal)
                    .padding(6.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = JarvisColors.Gunmetal.copy(alpha = 0.6f),
            modifier = Modifier.padding(start = 40.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(3) { index ->
                    val delay = index * 200
                    androidx.compose.animation.core.AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + scaleIn()
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = JarvisColors.BlueGlow.copy(alpha = 0.6f),
                            modifier = Modifier.size(6.dp)
                        ) {}
                    }
                }
            }
        }
    }
}
