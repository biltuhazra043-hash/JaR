package com.jarvis.ai.ui.files

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.jarvis.ai.ui.theme.JarvisColors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class FilesViewModel @Inject constructor() : ViewModel() {

    private val _categories = MutableStateFlow(
        listOf(
            FileCategory("Downloads", Icons.Default.Download, JarvisColors.BlueGlow, 0),
            FileCategory("Images", Icons.Default.Image, JarvisColors.Success, 0),
            FileCategory("Videos", Icons.Default.VideoLibrary, JarvisColors.Red, 0),
            FileCategory("Documents", Icons.Default.Description, JarvisColors.Gold, 0),
            FileCategory("Audio", Icons.Default.MusicNote, JarvisColors.ArcReactor, 0),
            FileCategory("Recent", Icons.Default.History, JarvisColors.TextSecondary, 0)
        )
    )
    val categories: StateFlow<List<FileCategory>> = _categories.asStateFlow()
}
