package com.jarvis.ai.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import java.text.SimpleDateFormat
import java.util.*

fun Context.isNetworkAvailable(): Boolean {
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
}

fun Date.formatTime(): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatDate(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return formatter.format(this)
}

fun Date.formatDateTime(): String {
    val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    return formatter.format(this)
}

fun String.capitalizeFirst(): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

fun Context.openApp(packageName: String): Boolean {
    return try {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        intent?.let {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(it)
            true
        } ?: false
    } catch (e: Exception) {
        false
    }
}

fun Context.getCurrentHour(): Int {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

fun getGreeting(context: Context): String {
    val hour = context.getCurrentHour()
    return when {
        hour in 5..11 -> "Good Morning, Sir."
        hour in 12..16 -> "Good Afternoon, Sir."
        hour in 17..20 -> "Good Evening, Sir."
        else -> "Good Night, Sir."
    }
}
