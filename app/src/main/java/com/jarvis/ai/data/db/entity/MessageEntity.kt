package com.jarvis.ai.data.db.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("conversationId")]
)
data class MessageEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val conversationId: String,
    val role: String, // "user", "assistant", "system"
    val content: String,
    val timestamp: Date = Date(),
    val isStreaming: Boolean = false,
    val tokensUsed: Int = 0
)
