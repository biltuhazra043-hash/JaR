package com.jarvis.ai.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val type: String, // time, location, battery, charging, custom
    val triggerConfig: String, // JSON trigger configuration
    val actions: String, // JSON list of actions
    val isEnabled: Boolean = true,
    val createdAt: Date = Date(),
    val lastTriggered: Date? = null
)
