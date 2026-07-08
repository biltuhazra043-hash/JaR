package com.jarvis.ai.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.jarvis.ai.service.JarvisService
import com.jarvis.ai.service.VoiceService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            // Start Jarvis service on boot
            context.startForegroundService(Intent(context, JarvisService::class.java))
        }
    }
}
