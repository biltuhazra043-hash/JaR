package com.jarvis.ai.data.db.dao

import androidx.room.*
import com.jarvis.ai.data.db.entity.AutomationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AutomationDao {

    @Query("SELECT * FROM automations ORDER BY createdAt DESC")
    fun getAllAutomations(): Flow<List<AutomationEntity>>

    @Query("SELECT * FROM automations WHERE isEnabled = 1")
    fun getEnabledAutomations(): Flow<List<AutomationEntity>>

    @Query("SELECT * FROM automations WHERE type = :type")
    fun getAutomationsByType(type: String): Flow<List<AutomationEntity>>

    @Query("SELECT * FROM automations WHERE id = :id")
    suspend fun getAutomationById(id: String): AutomationEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAutomation(automation: AutomationEntity): Long

    @Update
    suspend fun updateAutomation(automation: AutomationEntity)

    @Delete
    suspend fun deleteAutomation(automation: AutomationEntity)

    @Query("DELETE FROM automations")
    suspend fun deleteAllAutomations()
}
