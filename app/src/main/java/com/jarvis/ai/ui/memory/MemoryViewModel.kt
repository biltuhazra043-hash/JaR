package com.jarvis.ai.ui.memory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.data.repository.MemoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MemoryUiState(
    val id: String,
    val key: String,
    val value: String,
    val category: String
)

@HiltViewModel
class MemoryViewModel @Inject constructor(
    private val memoryRepository: MemoryRepository
) : ViewModel() {

    private val _memories = MutableStateFlow<List<MemoryUiState>>(emptyList())
    val memories: StateFlow<List<MemoryUiState>> = _memories.asStateFlow()

    private var currentCategory = "all"

    init {
        loadMemories()
    }

    private fun loadMemories() {
        viewModelScope.launch {
            if (currentCategory == "all") {
                memoryRepository.getAllMemories().collect { entities ->
                    _memories.value = entities.map {
                        MemoryUiState(
                            id = it.id,
                            key = it.key,
                            value = it.value,
                            category = it.category
                        )
                    }
                }
            } else {
                memoryRepository.getMemoriesByCategory(currentCategory).collect { entities ->
                    _memories.value = entities.map {
                        MemoryUiState(
                            id = it.id,
                            key = it.key,
                            value = it.value,
                            category = it.category
                        )
                    }
                }
            }
        }
    }

    fun search(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                loadMemories()
            } else {
                val results = memoryRepository.searchMemories(query)
                _memories.value = results.map {
                    MemoryUiState(
                        id = it.id,
                        key = it.key,
                        value = it.value,
                        category = it.category
                    )
                }
            }
        }
    }

    fun filterByCategory(category: String) {
        currentCategory = category
        loadMemories()
    }

    fun deleteMemory(id: String) {
        viewModelScope.launch {
            memoryRepository.deleteMemory(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            memoryRepository.deleteAllMemories()
        }
    }

    fun exportMemories() {
        viewModelScope.launch {
            val exported = memoryRepository.exportMemories()
            // In a real app, this would trigger a file save dialog
        }
    }
}
