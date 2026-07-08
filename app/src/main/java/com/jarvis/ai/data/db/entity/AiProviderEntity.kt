package com.jarvis.ai.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "ai_providers")
data class AiProviderEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val type: String, // "openrouter", "ollama", "custom"
    val apiKey: String = "",
    val baseUrl: String,
    val modelName: String,
    val temperature: Float = 0.7f,
    val topP: Float = 0.95f,
    val maxTokens: Int = 4096,
    val systemPrompt: String = "",
    val isStreaming: Boolean = true,
    val isEnabled: Boolean = false,
    val priority: Int = 0,
    val createdAt: Date = Date()
)
