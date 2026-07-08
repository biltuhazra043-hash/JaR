package com.jarvis.ai.domain.model

import java.util.Date

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: MessageRole,
    val content: String,
    val timestamp: Date = Date(),
    val isStreaming: Boolean = false
)

enum class MessageRole {
    USER, ASSISTANT, SYSTEM
}

data class Conversation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val messages: List<ChatMessage> = emptyList(),
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class Memory(
    val id: String = java.util.UUID.randomUUID().toString(),
    val category: MemoryCategory,
    val key: String,
    val value: String,
    val isImportant: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

enum class MemoryCategory {
    GENERAL, PREFERENCE, ROUTINE, NOTE, TASK
}

data class Automation(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val type: AutomationType,
    val triggerConfig: String,
    val actions: String,
    val isEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val lastTriggered: Date? = null
)

enum class AutomationType {
    TIME, LOCATION, BATTERY, CHARGING, CUSTOM
}

data class AiProvider(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val type: ProviderType,
    val apiKey: String = "",
    val baseUrl: String,
    val modelName: String,
    val temperature: Float = 0.7f,
    val topP: Float = 0.95f,
    val maxTokens: Int = 4096,
    val systemPrompt: String = "",
    val isStreaming: Boolean = true,
    val isEnabled: Boolean = false,
    val priority: Int = 0
)

enum class ProviderType {
    OPENROUTER, OLLAMA, CUSTOM
}
