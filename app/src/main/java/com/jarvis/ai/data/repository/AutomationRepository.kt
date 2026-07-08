package com.jarvis.ai.data.repository

import com.jarvis.ai.data.db.dao.AutomationDao
import com.jarvis.ai.data.db.entity.AutomationEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AutomationRepository @Inject constructor(
    private val automationDao: AutomationDao
) {
    fun getAllAutomations(): Flow<List<AutomationEntity>> = automationDao.getAllAutomations()

    fun getEnabledAutomations(): Flow<List<AutomationEntity>> = automationDao.getEnabledAutomations()

    fun getAutomationsByType(type: String): Flow<List<AutomationEntity>> =
        automationDao.getAutomationsByType(type)

    suspend fun getAutomationById(id: String): AutomationEntity? =
        automationDao.getAutomationById(id)

    suspend fun saveAutomation(automation: AutomationEntity): Long =
        automationDao.insertAutomation(automation)

    suspend fun updateAutomation(automation: AutomationEntity) =
        automationDao.updateAutomation(automation)

    suspend fun deleteAutomation(id: String) {
        automationDao.getAutomationById(id)?.let { automationDao.deleteAutomation(it) }
    }
}
