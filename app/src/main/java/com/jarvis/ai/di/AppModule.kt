package com.jarvis.ai.di

import android.content.Context
import androidx.room.Room
import com.jarvis.ai.data.db.JarvisDatabase
import com.jarvis.ai.data.db.dao.*
import com.jarvis.ai.data.preferences.JarvisPreferences
import com.jarvis.ai.data.remote.ApiClient
import com.jarvis.ai.data.repository.*
import com.jarvis.ai.device.DeviceController
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): JarvisDatabase {
        return Room.databaseBuilder(
            context,
            JarvisDatabase::class.java,
            "jarvis_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideConversationDao(db: JarvisDatabase): ConversationDao = db.conversationDao()

    @Provides
    @Singleton
    fun provideMemoryDao(db: JarvisDatabase): MemoryDao = db.memoryDao()

    @Provides
    @Singleton
    fun provideAutomationDao(db: JarvisDatabase): AutomationDao = db.automationDao()

    @Provides
    @Singleton
    fun provideAiProviderDao(db: JarvisDatabase): AiProviderDao = db.aiProviderDao()

    @Provides
    @Singleton
    fun provideJarvisPreferences(@ApplicationContext context: Context): JarvisPreferences {
        return JarvisPreferences(context)
    }

    @Provides
    @Singleton
    fun provideApiClient(preferences: JarvisPreferences): ApiClient {
        return ApiClient(preferences)
    }

    @Provides
    @Singleton
    fun provideDeviceController(@ApplicationContext context: Context): DeviceController {
        return DeviceController(context)
    }
}
