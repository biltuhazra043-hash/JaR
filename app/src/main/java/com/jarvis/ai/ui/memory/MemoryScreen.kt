package com.jarvis.ai.ui.memory

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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.jarvis.ai.ui.theme.JarvisColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemoryScreen(
    navController: NavController,
    viewModel: MemoryViewModel = hiltViewModel()
) {
    val memories by viewModel.memories.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("all") }

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
            Text("Memory", style = MaterialTheme.typography.titleMedium, color = JarvisColors.TextPrimary)
        }

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; viewModel.search(it) },
            placeholder = { Text("Search memories...", color = JarvisColors.TextTertiary) },
            leadingIcon = { Icon(Icons.Default.Search, null, tint = JarvisColors.TextTertiary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = JarvisColors.TextPrimary,
                unfocusedTextColor = JarvisColors.TextPrimary,
                focusedBorderColor = JarvisColors.BlueGlow,
                unfocusedBorderColor = JarvisColors.GunmetalLight,
                cursorColor = JarvisColors.BlueGlow
            ),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Category chips
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("all" to "All", "preference" to "Preferences", "note" to "Notes", "task" to "Tasks").forEach { (key, label) ->
                FilterChip(
                    selected = selectedCategory == key,
                    onClick = { selectedCategory = key; viewModel.filterByCategory(key) },
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

        Spacer(modifier = Modifier.height(12.dp))

        // Memories list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(memories) { memory ->
                MemoryCard(
                    key = memory.key,
                    value = memory.value,
                    category = memory.category,
                    onDelete = { viewModel.deleteMemory(memory.id) }
                )
            }

            if (memories.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Memory, null, tint = JarvisColors.TextTertiary, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("No memories yet", color = JarvisColors.TextTertiary)
                        }
                    }
                }
            }
        }

        // Action bar
        Row(
            modifier = Modifier.fillMaxWidth().background(JarvisColors.GunmetalDark).padding(12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(onClick = { viewModel.exportMemories() }) {
                Icon(Icons.Default.Download, null, tint = JarvisColors.BlueGlow, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", color = JarvisColors.BlueGlow)
            }
            TextButton(onClick = { viewModel.deleteAll() }) {
                Icon(Icons.Default.DeleteForever, null, tint = JarvisColors.Error, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Delete All", color = JarvisColors.Error)
            }
        }
    }
}

@Composable
fun MemoryCard(
    key: String,
    value: String,
    category: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = JarvisColors.Gunmetal.copy(alpha = 0.5f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = JarvisColors.BlueGlow.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = JarvisColors.BlueGlow,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(key, style = MaterialTheme.typography.titleSmall, color = JarvisColors.TextPrimary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(value, style = MaterialTheme.typography.bodySmall, color = JarvisColors.TextSecondary)
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, "Delete", tint = JarvisColors.TextTertiary, modifier = Modifier.size(16.dp))
            }
        }
    }
}
