package com.jarvis.ai.device

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.AlarmClock
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.telephony.SmsManager
import android.webkit.MimeTypeMap
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceController @Inject constructor(
    private val context: Context
) {
    // ---- WiFi ----
    fun toggleWifi(): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val newState = !wifiManager.isWifiEnabled
        wifiManager.isWifiEnabled = newState
        return newState
    }

    fun isWifiEnabled(): Boolean {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        return wifiManager.isWifiEnabled
    }

    // ---- Bluetooth ----
    fun toggleBluetooth(): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter = bluetoothManager.adapter ?: return false
        val newState = !adapter.isEnabled
        if (newState) adapter.enable() else adapter.disable()
        return newState
    }

    fun isBluetoothEnabled(): Boolean {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return bluetoothManager.adapter?.isEnabled == true
    }

    // ---- Volume ----
    fun setVolume(stream: Int = AudioManager.STREAM_MUSIC, level: Int) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val max = audioManager.getStreamMaxVolume(stream)
        val clampedLevel = level.coerceIn(0, max)
        audioManager.setStreamVolume(stream, clampedLevel, AudioManager.FLAG_SHOW_UI)
    }

    fun getVolume(stream: Int = AudioManager.STREAM_MUSIC): Int {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return audioManager.getStreamVolume(stream)
    }

    // ---- Brightness ----
    fun setBrightness(level: Int) {
        val clampedLevel = level.coerceIn(0, 255)
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        )
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            clampedLevel
        )
    }

    // ---- Silent / DND ----
    fun toggleSilentMode() {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentMode = audioManager.ringerMode
        audioManager.ringerMode = if (currentMode == AudioManager.RINGER_MODE_SILENT) {
            AudioManager.RINGER_MODE_NORMAL
        } else {
            AudioManager.RINGER_MODE_SILENT
        }
    }

    fun toggleDndMode() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val currentMode = notificationManager.currentInterruptionFilter
        notificationManager.setInterruptionFilter(
            if (currentMode == NotificationManager.INTERRUPTION_FILTER_NONE) {
                NotificationManager.INTERRUPTION_FILTER_ALL
            } else {
                NotificationManager.INTERRUPTION_FILTER_NONE
            }
        )
    }

    // ---- SMS ----
    fun sendSms(phoneNumber: String, message: String): Boolean {
        return try {
            @Suppress("DEPRECATION")
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, message, null, null)
            true
        } catch (e: Exception) {
            false
        }
    }

    // ---- Phone Call ----
    fun makeCall(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_CALL).apply {
            data = Uri.parse("tel:$phoneNumber")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // ---- Alarm ----
    fun setAlarm(hour: Int, minute: Int, message: String = "") {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTE, minute)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // ---- Timer ----
    fun setTimer(seconds: Int, message: String = "") {
        val intent = Intent(AlarmClock.ACTION_SET_TIMER).apply {
            putExtra(AlarmClock.EXTRA_LENGTH, seconds)
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // ---- Flashlight ----
    @Suppress("DEPRECATION")
    fun toggleFlashlight(): Boolean {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
        val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
        val newEnabled = !isFlashlightOn()
        try {
            cameraManager.setTorchMode(cameraId, newEnabled)
        } catch (_: Exception) {}
        return newEnabled
    }

    private fun isFlashlightOn(): Boolean {
        return try {
            val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
            val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return false
            // Android API doesn't directly expose torch state, we track it manually
            false
        } catch (e: Exception) {
            false
        }
    }

    // ---- System Info ----
    fun getBatteryInfo(): Map<String, Any> {
        val batteryIntent = context.registerReceiver(null, android.content.IntentFilter(android.content.Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_SCALE, -1) ?: -1
        val percentage = if (level != -1 && scale != -1) (level * 100) / scale else 0
        return mapOf(
            "percentage" to percentage,
            "charging" to (batteryIntent?.getIntExtra(android.os.BatteryManager.EXTRA_STATUS, -1) == android.os.BatteryManager.BATTERY_STATUS_CHARGING)
        )
    }

    fun getStorageInfo(): Map<String, String> {
        val stat = StatFs(Environment.getDataDirectory().path)
        val totalBytes = stat.totalBytes
        val availableBytes = stat.availableBlocksLong * stat.blockSizeLong
        val usedBytes = totalBytes - availableBytes
        return mapOf(
            "total" to formatBytes(totalBytes),
            "used" to formatBytes(usedBytes),
            "available" to formatBytes(availableBytes)
        )
    }

    fun getRamInfo(): Map<String, String> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        return mapOf(
            "total" to formatBytes(memInfo.totalMem),
            "available" to formatBytes(memInfo.availMem),
            "used" to formatBytes(memInfo.totalMem - memInfo.availMem),
            "threshold" to formatBytes(memInfo.threshold)
        )
    }

    private fun formatBytes(bytes: Long): String {
        val gb = bytes / (1024.0 * 1024.0 * 1024.0)
        return if (gb >= 1) String.format("%.1f GB", gb) else String.format("%.0f MB", bytes / (1024.0 * 1024.0))
    }

    // ---- Contacts ----
    fun getContactNames(): List<String> {
        val contacts = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                contacts.add(name)
            }
        }
        return contacts
    }

    // ---- Open Apps ----
    fun openApp(appName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(appName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
            return true
        }
        // Try to find app by name
        val pm = context.packageManager
        val apps = pm.getInstalledApplications(0)
        for (appInfo in apps) {
            val label = pm.getApplicationLabel(appInfo).toString()
            if (label.equals(appName, ignoreCase = true)) {
                val launchIntent = pm.getLaunchIntentForPackage(appInfo.packageName)
                launchIntent?.let {
                    it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(it)
                    return true
                }
            }
        }
        return false
    }

    fun getInstalledApps(): List<String> {
        val pm = context.packageManager
        return pm.getInstalledApplications(0).map {
            pm.getApplicationLabel(it).toString()
        }.sorted()
    }

    // ---- Search ----
    fun openBrowserSearch(query: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openYouTubeSearch(query: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=${Uri.encode(query)}")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun openMapsSearch(query: String) {
        val uri = Uri.parse("geo:0,0?q=${Uri.encode(query)}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    // ---- Clipboard ----
    fun getClipboardText(): String {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString() ?: ""
    }

    fun setClipboardText(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Jarvis", text)
        clipboard.setPrimaryClip(clip)
    }

    // ---- File operations ----
    fun searchFiles(directory: File, query: String): List<File> {
        val results = mutableListOf<File>()
        directory.walk().maxDepth(3).forEach { file ->
            if (file.name.contains(query, ignoreCase = true)) {
                results.add(file)
                if (results.size >= 50) return results
            }
        }
        return results
    }
}
