package com.jarvis.ai.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "conversations")
data class ConversationEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String = "New Conversation",
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isActive: Boolean = true
)
