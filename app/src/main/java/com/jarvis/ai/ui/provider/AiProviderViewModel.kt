package com.jarvis.ai.ui.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.data.db.entity.AiProviderEntity
import com.jarvis.ai.data.repository.AiProviderRepository
import com.jarvis.ai.data.remote.ApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderUiState(
    val id: String,
    val name: String,
    val type: String,
    val apiKey: String,
    val baseUrl: String,
    val modelName: String,
    val temperature: Float,
    val topP: Float,
    val maxTokens: Int,
    val systemPrompt: String,
    val isActive: Boolean,
    val isConnected: Boolean,
    val isStreaming: Boolean,
    val priority: Int
)

@HiltViewModel
class AiProviderViewModel @Inject constructor(
    private val aiProviderRepository: AiProviderRepository,
    private val apiClient: ApiClient
) : ViewModel() {

    private val _providers = MutableStateFlow<List<ProviderUiState>>(emptyList())
    val providers: StateFlow<List<ProviderUiState>> = _providers.asStateFlow()

    init {
        viewModelScope.launch {
            aiProviderRepository.getAllProviders().collect { entities ->
                _providers.value = entities.map { entity ->
                    ProviderUiState(
                        id = entity.id,
                        name = entity.name,
                        type = entity.type,
                        apiKey = entity.apiKey,
                        baseUrl = entity.baseUrl,
                        modelName = entity.modelName,
                        temperature = entity.temperature,
                        topP = entity.topP,
                        maxTokens = entity.maxTokens,
                        systemPrompt = entity.systemPrompt,
                        isActive = entity.isEnabled,
                        isConnected = false,
                        isStreaming = entity.isStreaming,
                        priority = entity.priority
                    )
                }
            }
        }
    }

    fun addProvider(name: String, type: String, apiKey: String, baseUrl: String, model: String) {
        viewModelScope.launch {
            val entity = AiProviderEntity(
                name = name,
                type = type,
                apiKey = apiKey,
                baseUrl = baseUrl,
                modelName = model
            )
            aiProviderRepository.saveProvider(entity)
        }
    }

    fun updateProvider(
        id: String,
        name: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        temperature: Float,
        maxTokens: Int,
        systemPrompt: String
    ) {
        viewModelScope.launch {
            val current = _providers.value.find { it.id == id } ?: return@launch
            val entity = AiProviderEntity(
                id = id,
                name = name,
                type = current.type,
                apiKey = apiKey,
                baseUrl = baseUrl,
                modelName = model,
                temperature = temperature,
                topP = current.topP,
                maxTokens = maxTokens,
                systemPrompt = systemPrompt,
                isStreaming = current.isStreaming,
                isEnabled = current.isActive,
                priority = current.priority
            )
            aiProviderRepository.saveProvider(entity)
        }
    }

    fun activateProvider(id: String) {
        viewModelScope.launch {
            aiProviderRepository.activateProvider(id)
        }
    }

    fun deleteProvider(id: String) {
        viewModelScope.launch {
            aiProviderRepository.deleteProvider(id)
        }
    }

    fun testConnection(id: String) {
        viewModelScope.launch {
            val provider = _providers.value.find { it.id == id } ?: return@launch
            val result = apiClient.testConnection(
                baseUrl = provider.baseUrl,
                apiKey = provider.apiKey,
                model = provider.modelName
            )
            result.fold(
                onSuccess = {
                    _providers.update { list ->
                        list.map { if (it.id == id) it.copy(isConnected = true) else it }
                    }
                },
                onFailure = {
                    _providers.update { list ->
                        list.map { if (it.id == id) it.copy(isConnected = false) else it }
                    }
                }
            )
        }
    }
}
