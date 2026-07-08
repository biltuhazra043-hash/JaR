package com.jarvis.ai.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "memories")
data class MemoryEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val category: String = "general", // general, preference, routine, note, task
    val key: String,
    val value: String,
    val embedding: String? = null, // For semantic search
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isImportant: Boolean = false
)
