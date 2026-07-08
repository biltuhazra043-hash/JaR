package com.jarvis.ai.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.data.preferences.JarvisPreferences
import com.jarvis.ai.data.repository.ChatRepository
import com.jarvis.ai.util.getGreeting
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "Good Morning, Sir.",
    val statusText: String = "At your service.",
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val isProcessing: Boolean = false,
    val lastSpokenText: String = ""
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val preferences: JarvisPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            // Update greeting based on time
            _uiState.update {
                it.copy(greeting = getGreetingFromHour())
            }
        }
    }

    private fun getGreetingFromHour(): String {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        return when {
            hour in 5..11 -> "Good Morning, Sir."
            hour in 12..16 -> "Good Afternoon, Sir."
            hour in 17..20 -> "Good Evening, Sir."
            else -> "Good Night, Sir."
        }
    }

    fun toggleListening() {
        val currentlyListening = _uiState.value.isListening
        if (currentlyListening) {
            _uiState.update { it.copy(isListening = false, statusText = "Standing by") }
        } else {
            _uiState.update { it.copy(isListening = true, statusText = "Listening...") }
        }
    }

    fun onSpeechResult(text: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isListening = false,
                    isProcessing = true,
                    statusText = "Processing..."
                )
            }

            // Process the speech through AI
            val conversationId = chatRepository.getOrCreateActiveConversation()
            chatRepository.addUserMessage(conversationId, text)

            val result = chatRepository.getAiResponse(text, conversationId)
            result.fold(
                onSuccess = { response ->
                    chatRepository.addAssistantMessage(conversationId, response)
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            isSpeaking = true,
                            statusText = "Speaking...",
                            lastSpokenText = response
                        )
                    }
                    // After speaking completes, go back to idle
                    _uiState.update {
                        it.copy(
                            isSpeaking = false,
                            statusText = "Standing by"
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            statusText = "Error occurred. Try again."
                        )
                    }
                }
            )
        }
    }
}
