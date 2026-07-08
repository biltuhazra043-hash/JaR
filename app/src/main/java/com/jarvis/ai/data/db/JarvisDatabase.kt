package com.jarvis.ai.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.jarvis.ai.data.db.dao.*
import com.jarvis.ai.data.db.entity.*

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        MemoryEntity::class,
        AutomationEntity::class,
        AiProviderEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class JarvisDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun memoryDao(): MemoryDao
    abstract fun automationDao(): AutomationDao
    abstract fun aiProviderDao(): AiProviderDao
}
