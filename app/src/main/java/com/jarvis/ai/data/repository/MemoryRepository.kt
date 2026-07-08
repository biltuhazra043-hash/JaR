package com.jarvis.ai.data.repository

import com.jarvis.ai.data.db.dao.MemoryDao
import com.jarvis.ai.data.db.entity.MemoryEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemoryRepository @Inject constructor(
    private val memoryDao: MemoryDao
) {
    fun getAllMemories(): Flow<List<MemoryEntity>> = memoryDao.getAllMemories()

    fun getMemoriesByCategory(category: String): Flow<List<MemoryEntity>> =
        memoryDao.getMemoriesByCategory(category)

    fun getImportantMemories(): Flow<List<MemoryEntity>> = memoryDao.getImportantMemories()

    suspend fun searchMemories(query: String): List<MemoryEntity> =
        memoryDao.searchMemories(query)

    suspend fun getMemory(key: String): MemoryEntity? = memoryDao.getMemoryByKey(key)

    suspend fun saveMemory(key: String, value: String, category: String = "general", isImportant: Boolean = false) {
        val existing = memoryDao.getMemoryByCategoryAndKey(category, key)
        if (existing != null) {
            memoryDao.updateMemory(existing.copy(value = value, updatedAt = java.util.Date()))
        } else {
            memoryDao.insertMemory(
                MemoryEntity(
                    key = key,
                    value = value,
                    category = category,
                    isImportant = isImportant
                )
            )
        }
    }

    suspend fun deleteMemory(id: String) {
        memoryDao.getMemoryById(id)?.let { memoryDao.deleteMemory(it) }
    }

    suspend fun deleteAllMemories() = memoryDao.deleteAllMemories()

    suspend fun exportMemories(): String {
        val all = memoryDao.searchMemories("")
        return all.joinToString("\n") { "${it.category}|${it.key}|${it.value}" }
    }
}
