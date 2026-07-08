package com.jarvis.ai.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jarvis.ai.data.repository.ChatRepository
import com.jarvis.ai.domain.model.ChatMessage
import com.jarvis.ai.domain.model.MessageRole
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing.asStateFlow()

    private var conversationId: String? = null

    init {
        viewModelScope.launch {
            conversationId = chatRepository.getOrCreateActiveConversation()
            conversationId?.let { id ->
                chatRepository.getMessages(id).collect { dbMessages ->
                    _messages.value = dbMessages.map {
                        ChatMessage(
                            id = it.id,
                            role = when (it.role) {
                                "user" -> MessageRole.USER
                                "assistant" -> MessageRole.ASSISTANT
                                else -> MessageRole.SYSTEM
                            },
                            content = it.content,
                            timestamp = it.timestamp
                        )
                    }
                }
            }
        }
    }

    fun sendMessage() {
        val text = _inputText.value.trim()
        if (text.isBlank()) return

        viewModelScope.launch {
            val convId = conversationId ?: chatRepository.getOrCreateActiveConversation()
            conversationId = convId

            // Add user message
            _messages.value = _messages.value + ChatMessage(
                role = MessageRole.USER,
                content = text
            )
            _inputText.value = ""
            _isProcessing.value = true

            chatRepository.addUserMessage(convId, text)

            // Get AI response
            val result = chatRepository.getAiResponse(text, convId)
            result.fold(
                onSuccess = { response ->
                    chatRepository.addAssistantMessage(convId, response)
                    _isProcessing.value = false
                },
                onFailure = { error ->
                    _messages.value = _messages.value + ChatMessage(
                        role = MessageRole.ASSISTANT,
                        content = "I'm sorry, Sir. An error occurred: ${error.message}"
                    )
                    _isProcessing.value = false
                }
            )
        }
    }

    fun updateInputText(text: String) {
        _inputText.value = text
    }

    fun startVoiceInput() {
        // Voice input handled by VoiceService
    }
}
