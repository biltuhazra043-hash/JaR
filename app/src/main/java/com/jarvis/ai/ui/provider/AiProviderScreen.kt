package com.jarvis.ai.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jarvis.ai.ui.theme.JarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiProviderScreen(
    navController: NavController,
    viewModel: AiProviderViewModel = hiltViewModel()
) {
    val providers by viewModel.providers.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedProviderId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(JarvisColors.DeepBlack, JarvisColors.DarkBg)
                )
            )
    ) {
        // Top Bar
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
                    text = "AI Provider",
                    style = MaterialTheme.typography.titleMedium,
                    color = JarvisColors.TextPrimary
                )
                Text(
                    text = "Configure OpenRouter, Ollama, Custom APIs",
                    style = MaterialTheme.typography.bodySmall,
                    color = JarvisColors.TextTertiary
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Provider cards
            providers.forEach { provider ->
                ProviderCard(
                    name = provider.name,
                    type = provider.type,
                    model = provider.modelName,
                    isActive = provider.isActive,
                    isConnected = provider.isConnected,
                    onActivate = { viewModel.activateProvider(provider.id) },
                    onEdit = { selectedProviderId = provider.id },
                    onDelete = { viewModel.deleteProvider(provider.id) },
                    onTest = { viewModel.testConnection(provider.id) }
                )
            }

            // Add Provider Button
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddDialog = true },
                colors = CardDefaults.cardColors(
                    containerColor = JarvisColors.BlueGlow.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Provider",
                        tint = JarvisColors.BlueGlow
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add AI Provider",
                        style = MaterialTheme.typography.bodyLarge,
                        color = JarvisColors.BlueGlow
                    )
                }
            }
        }
    }

    // Add Provider Dialog
    if (showAddDialog) {
        AddProviderDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, type, apiKey, baseUrl, model ->
                viewModel.addProvider(name, type, apiKey, baseUrl, model)
                showAddDialog = false
            }
        )
    }

    // Edit Provider Dialog
    selectedProviderId?.let { id ->
        val provider = providers.find { it.id == id }
        if (provider != null) {
            EditProviderDialog(
                provider = provider,
                onDismiss = { selectedProviderId = null },
                onSave = { name, apiKey, baseUrl, model, temp, maxTok, sysPrompt ->
                    viewModel.updateProvider(id, name, apiKey, baseUrl, model, temp, maxTok, sysPrompt)
                    selectedProviderId = null
                }
            )
        }
    }
}

@Composable
fun ProviderCard(
    name: String,
    type: String,
    model: String,
    isActive: Boolean,
    isConnected: Boolean,
    onActivate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTest: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) JarvisColors.BlueGlow.copy(alpha = 0.1f) else JarvisColors.Gunmetal.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (type.lowercase()) {
                            "openrouter" -> Icons.Default.Cloud
                            "ollama" -> Icons.Default.Dns
                            else -> Icons.Default.Api
                        },
                        contentDescription = null,
                        tint = if (isActive) JarvisColors.BlueGlow else JarvisColors.TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name,
                            style = MaterialTheme.typography.titleMedium,
                            color = JarvisColors.TextPrimary
                        )
                        Text(
                            text = model,
                            style = MaterialTheme.typography.bodySmall,
                            color = JarvisColors.TextTertiary
                        )
                    }
                }

                Row {
                    if (isActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = JarvisColors.Success.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                color = JarvisColors.Success,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    if (isConnected) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = "Connected",
                            tint = JarvisColors.Success,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!isActive) {
                    TextButton(
                        onClick = onActivate,
                        colors = ButtonDefaults.textButtonColors(contentColor = JarvisColors.BlueGlow)
                    ) {
                        Text("Activate", style = MaterialTheme.typography.labelMedium)
                    }
                }
                TextButton(
                    onClick = onTest,
                    colors = ButtonDefaults.textButtonColors(contentColor = JarvisColors.TextSecondary)
                ) {
                    Text("Test", style = MaterialTheme.typography.labelMedium)
                }
                TextButton(
                    onClick = onEdit,
                    colors = ButtonDefaults.textButtonColors(contentColor = JarvisColors.TextSecondary)
                ) {
                    Text("Edit", style = MaterialTheme.typography.labelMedium)
                }
                TextButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.textButtonColors(contentColor = JarvisColors.Error)
                ) {
                    Text("Delete", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}

@Composable
fun AddProviderDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, type: String, apiKey: String, baseUrl: String, model: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("openrouter") }
    var apiKey by remember { mutableStateOf("") }
    var baseUrl by remember { mutableStateOf("https://openrouter.ai/api/v1") }
    var model by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = JarvisColors.Gunmetal,
        title = {
            Text("Add AI Provider", color = JarvisColors.TextPrimary)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Provider Name", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Type selector
                Text("Type", style = MaterialTheme.typography.labelMedium, color = JarvisColors.TextSecondary)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("openrouter" to "OpenRouter", "ollama" to "Ollama", "custom" to "Custom").forEach { (value, label) ->
                        FilterChip(
                            selected = type == value,
                            onClick = { type = value },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = JarvisColors.BlueGlow.copy(alpha = 0.2f),
                                selectedLabelColor = JarvisColors.BlueGlow,
                                containerColor = JarvisColors.GunmetalLight,
                                labelColor = JarvisColors.TextSecondary
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("Base URL", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key (optional)", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model Name", color = JarvisColors.TextTertiary) },
                    placeholder = { Text("e.g., openai/gpt-4, llama3, etc.", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(name, type, apiKey, baseUrl, model) },
                enabled = name.isNotBlank() && baseUrl.isNotBlank() && model.isNotBlank()
            ) {
                Text("Save", color = JarvisColors.BlueGlow)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = JarvisColors.TextTertiary)
            }
        }
    )
}

@Composable
fun EditProviderDialog(
    provider: ProviderUiState,
    onDismiss: () -> Unit,
    onSave: (name: String, apiKey: String, baseUrl: String, model: String, temperature: Float, maxTokens: Int, systemPrompt: String) -> Unit
) {
    var name by remember { mutableStateOf(provider.name) }
    var apiKey by remember { mutableStateOf(provider.apiKey) }
    var baseUrl by remember { mutableStateOf(provider.baseUrl) }
    var model by remember { mutableStateOf(provider.modelName) }
    var temperature by remember { mutableStateOf(provider.temperature) }
    var maxTokens by remember { mutableStateOf(provider.maxTokens.toString()) }
    var systemPrompt by remember { mutableStateOf(provider.systemPrompt) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = JarvisColors.Gunmetal,
        title = {
            Text("Edit ${provider.name}", color = JarvisColors.TextPrimary)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = { baseUrl = it },
                    label = { Text("Base URL", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API Key", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Model", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                // Temperature slider
                Text("Temperature: ${"%.2f".format(temperature)}", color = JarvisColors.TextSecondary, style = MaterialTheme.typography.labelMedium)
                Slider(
                    value = temperature,
                    onValueChange = { temperature = it },
                    valueRange = 0f..2f,
                    steps = 20,
                    colors = SliderDefaults.colors(
                        thumbColor = JarvisColors.BlueGlow,
                        activeTrackColor = JarvisColors.BlueGlow
                    )
                )

                OutlinedTextField(
                    value = maxTokens,
                    onValueChange = { maxTokens = it },
                    label = { Text("Max Tokens", color = JarvisColors.TextTertiary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = systemPrompt,
                    onValueChange = { systemPrompt = it },
                    label = { Text("System Prompt", color = JarvisColors.TextTertiary) },
                    minLines = 3,
                    maxLines = 8,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = JarvisColors.TextPrimary,
                        unfocusedTextColor = JarvisColors.TextPrimary,
                        focusedBorderColor = JarvisColors.BlueGlow,
                        unfocusedBorderColor = JarvisColors.GunmetalLight,
                        cursorColor = JarvisColors.BlueGlow
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(name, apiKey, baseUrl, model, temperature, maxTokens.toIntOrNull() ?: 4096, systemPrompt)
                }
            ) {
                Text("Save", color = JarvisColors.BlueGlow)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = JarvisColors.TextTertiary)
            }
        }
    )
}
