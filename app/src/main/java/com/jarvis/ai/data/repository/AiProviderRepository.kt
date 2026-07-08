package com.jarvis.ai.data.repository

import com.jarvis.ai.data.db.dao.AiProviderDao
import com.jarvis.ai.data.db.entity.AiProviderEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiProviderRepository @Inject constructor(
    private val aiProviderDao: AiProviderDao
) {
    fun getAllProviders(): Flow<List<AiProviderEntity>> = aiProviderDao.getAllProviders()

    fun getActiveProvider(): Flow<AiProviderEntity?> = aiProviderDao.getActiveProvider()

    suspend fun getActiveProviderSync(): AiProviderEntity? = aiProviderDao.getActiveProviderSync()

    suspend fun getProviderById(id: String): AiProviderEntity? = aiProviderDao.getProviderById(id)

    suspend fun saveProvider(provider: AiProviderEntity): Long =
        aiProviderDao.insertProvider(provider)

    suspend fun activateProvider(id: String) {
        aiProviderDao.disableAllProviders()
        aiProviderDao.activateProvider(id)
    }

    suspend fun deleteProvider(id: String) {
        aiProviderDao.getProviderById(id)?.let { aiProviderDao.deleteProvider(it) }
    }

    suspend fun toggleProvider(id: String, enabled: Boolean) {
        aiProviderDao.getProviderById(id)?.let { provider ->
            aiProviderDao.updateProvider(provider.copy(isEnabled = enabled))
        }
    }
}
