package com.jarvis.ai.data.db.dao

import androidx.room.*
import com.jarvis.ai.data.db.entity.AiProviderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AiProviderDao {

    @Query("SELECT * FROM ai_providers ORDER BY priority DESC")
    fun getAllProviders(): Flow<List<AiProviderEntity>>

    @Query("SELECT * FROM ai_providers WHERE isEnabled = 1 ORDER BY priority DESC LIMIT 1")
    fun getActiveProvider(): Flow<AiProviderEntity?>

    @Query("SELECT * FROM ai_providers WHERE isEnabled = 1 ORDER BY priority DESC LIMIT 1")
    suspend fun getActiveProviderSync(): AiProviderEntity?

    @Query("SELECT * FROM ai_providers WHERE id = :id")
    suspend fun getProviderById(id: String): AiProviderEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProvider(provider: AiProviderEntity): Long

    @Update
    suspend fun updateProvider(provider: AiProviderEntity)

    @Delete
    suspend fun deleteProvider(provider: AiProviderEntity)

    @Query("DELETE FROM ai_providers")
    suspend fun deleteAllProviders()

    @Query("UPDATE ai_providers SET isEnabled = 0")
    suspend fun disableAllProviders()

    @Query("UPDATE ai_providers SET isEnabled = 1, priority = 1 WHERE id = :id")
    suspend fun activateProvider(id: String)
}
