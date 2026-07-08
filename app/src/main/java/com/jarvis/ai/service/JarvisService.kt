package com.jarvis.ai.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.jarvis.ai.JarvisApp
import com.jarvis.ai.R
import com.jarvis.ai.ui.MainActivity
import com.jarvis.ai.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JarvisService : Service() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(Constants.NOTIFICATION_ID_SERVICE, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, JarvisApp.CHANNEL_SERVICE)
            .setContentTitle("Jarvis")
            .setContentText("Running in background")
            .setSmallIcon(R.drawable.ic_arc_reactor)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .build()
    }

    override fun onDestroy() {
        instance = null
        super.onDestroy()
    }

    companion object {
        private var instance: JarvisService? = null

        fun isRunning(): Boolean = instance != null
    }
}
