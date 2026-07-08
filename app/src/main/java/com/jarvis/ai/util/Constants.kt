package com.jarvis.ai.util

object Constants {
    // Wake word
    const val WAKE_WORD = "hey jarvis"
    const val WAKE_WORD_CONFIDENCE_THRESHOLD = 0.7f

    // API defaults
    const val DEFAULT_OPENROUTER_URL = "https://openrouter.ai/api/v1"
    const val DEFAULT_OLLAMA_URL = "http://localhost:11434/v1"

    // TTS defaults
    const val DEFAULT_TTS_SPEED = 1.0f
    const val DEFAULT_TTS_PITCH = 0.9f

    // Languages
    const val LANG_ENGLISH = "en"
    const val LANG_HINDI = "hi"
    const val LANG_BENGALI = "bn"

    // Notification IDs
    const val NOTIFICATION_ID_VOICE = 1001
    const val NOTIFICATION_ID_SERVICE = 1002
    const val NOTIFICATION_ID_ALERT = 1003

    // Intent Actions
    const val ACTION_START_LISTENING = "com.jarvis.ai.START_LISTENING"
    const val ACTION_STOP_LISTENING = "com.jarvis.ai.STOP_LISTENING"
    const val ACTION_WAKE_WORD_DETECTED = "com.jarvis.ai.WAKE_WORD_DETECTED"
    const val ACTION_SPEAK = "com.jarvis.ai.SPEAK"

    // Device commands
    object Commands {
        const val OPEN_APP = "open_app"
        const val SET_BRIGHTNESS = "set_brightness"
        const val SET_VOLUME = "set_volume"
        const val TOGGLE_WIFI = "toggle_wifi"
        const val TOGGLE_BLUETOOTH = "toggle_bluetooth"
        const val TOGGLE_FLASHLIGHT = "toggle_flashlight"
        const val TOGGLE_SILENT = "toggle_silent"
        const val TOGGLE_DND = "toggle_dnd"
        const val SEND_SMS = "send_sms"
        const val MAKE_CALL = "make_call"
        const val SET_ALARM = "set_alarm"
        const val SET_REMINDER = "set_reminder"
        const val PLAY_MEDIA = "play_media"
        const val PAUSE_MEDIA = "pause_media"
        const val NEXT_MEDIA = "next_media"
        const val PREVIOUS_MEDIA = "previous_media"
        const val TAKE_SCREENSHOT = "take_screenshot"
        const val SEARCH = "search"
        const val TRANSLATE = "translate"
    }
}
