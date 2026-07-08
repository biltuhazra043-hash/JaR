package com.jarvis.ai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.content.ContextCompat
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class JarvisApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val voiceChannel = NotificationChannel(
            CHANNEL_VOICE,
            getString(R.string.notification_channel_voice),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.notification_channel_voice_desc)
            setShowBadge(false)
        }

        val serviceChannel = NotificationChannel(
            CHANNEL_SERVICE,
            getString(R.string.notification_channel_service),
            NotificationManager.IMPORTANCE_MIN
        ).apply {
            description = getString(R.string.notification_channel_service_desc)
            setShowBadge(false)
        }

        val alertChannel = NotificationChannel(
            CHANNEL_ALERTS,
            getString(R.string.notification_channel_alerts),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = getString(R.string.notification_channel_alerts_desc)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(voiceChannel)
        manager.createNotificationChannel(serviceChannel)
        manager.createNotificationChannel(alertChannel)
    }

    companion object {
        lateinit var instance: JarvisApp
            private set

        const val CHANNEL_VOICE = "jarvis_voice"
        const val CHANNEL_SERVICE = "jarvis_service"
        const val CHANNEL_ALERTS = "jarvis_alerts"
    }
}
