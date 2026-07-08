package com.jarvis.ai.service

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class JarvisNotificationListener : android.service.notification.NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        sbn ?: return
        val notification = sbn.notification
        val extras = notification.extras
        val title = extras.getString(Notification.EXTRA_TITLE) ?: ""
        val text = extras.getString(Notification.EXTRA_TEXT) ?: ""
        val packageName = sbn.packageName

        // Skip our own notifications
        if (packageName == applicationContext.packageName) return

        val notificationData = NotificationData(
            packageName = packageName,
            title = title,
            text = text,
            timestamp = sbn.postTime
        )

        // Store notification for AI processing
        _notifications.tryEmit(notificationData)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        // Handle notification removal
    }

    companion object {
        private val _notifications = MutableSharedFlow<NotificationData>(replay = 0)
        val notifications: SharedFlow<NotificationData> = _notifications

        fun isPermissionGranted(context: Context): Boolean {
            val componentName = ComponentName(
                context,
                JarvisNotificationListener::class.java
            )
            val flat = Settings.Secure.getString(
                context.contentResolver,
                "enabled_notification_listeners"
            )
            return flat?.contains(componentName.flattenToString()) == true
        }
    }
}

data class NotificationData(
    val packageName: String,
    val title: String,
    val text: String,
    val timestamp: Long
)
