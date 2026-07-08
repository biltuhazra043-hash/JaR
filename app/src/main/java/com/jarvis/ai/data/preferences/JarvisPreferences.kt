package com.jarvis.ai.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jarvis_prefs")

class JarvisPreferences(private val context: Context) {

    private val dataStore = context.dataStore

    // Language
    val language: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "en" }

    // Voice
    val voiceEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_VOICE_ENABLED] ?: true }
    val wakeWordEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_WAKE_WORD_ENABLED] ?: true }
    val ttsSpeed: Flow<Float> = dataStore.data.map { it[KEY_TTS_SPEED] ?: 1.0f }
    val ttsPitch: Flow<Float> = dataStore.data.map { it[KEY_TTS_PITCH] ?: 0.9f }

    // Theme
    val themeMode: Flow<String> = dataStore.data.map { it[KEY_THEME_MODE] ?: "dark" }
    val accentColor: Flow<Long> = dataStore.data.map { it[KEY_ACCENT_COLOR] ?: 0xFF00B0FF }

    // Performance
    val animationQuality: Flow<String> = dataStore.data.map { it[KEY_ANIMATION_QUALITY] ?: "high" }
    val batterySaver: Flow<Boolean> = dataStore.data.map { it[KEY_BATTERY_SAVER] ?: false }

    // Security
    val biometricEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_BIOMETRIC_ENABLED] ?: false }
    val pinEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_PIN_ENABLED] ?: false }

    // Telegram
    val telegramEnabled: Flow<Boolean> = dataStore.data.map { it[KEY_TELEGRAM_ENABLED] ?: false }
    val telegramBotToken: Flow<String> = dataStore.data.map { it[KEY_TELEGRAM_BOT_TOKEN] ?: "" }
    val telegramChatId: Flow<String> = dataStore.data.map { it[KEY_TELEGRAM_CHAT_ID] ?: "" }

    // AI System Prompt
    val systemPrompt: Flow<String> = dataStore.data.map {
        it[KEY_SYSTEM_PROMPT] ?: DEFAULT_SYSTEM_PROMPT
    }

    // First Launch
    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { it[KEY_FIRST_LAUNCH] ?: true }

    suspend fun setLanguage(value: String) { dataStore.edit { it[KEY_LANGUAGE] = value } }
    suspend fun setVoiceEnabled(value: Boolean) { dataStore.edit { it[KEY_VOICE_ENABLED] = value } }
    suspend fun setWakeWordEnabled(value: Boolean) { dataStore.edit { it[KEY_WAKE_WORD_ENABLED] = value } }
    suspend fun setTtsSpeed(value: Float) { dataStore.edit { it[KEY_TTS_SPEED] = value } }
    suspend fun setTtsPitch(value: Float) { dataStore.edit { it[KEY_TTS_PITCH] = value } }
    suspend fun setThemeMode(value: String) { dataStore.edit { it[KEY_THEME_MODE] = value } }
    suspend fun setAccentColor(value: Long) { dataStore.edit { it[KEY_ACCENT_COLOR] = value } }
    suspend fun setAnimationQuality(value: String) { dataStore.edit { it[KEY_ANIMATION_QUALITY] = value } }
    suspend fun setBatterySaver(value: Boolean) { dataStore.edit { it[KEY_BATTERY_SAVER] = value } }
    suspend fun setBiometricEnabled(value: Boolean) { dataStore.edit { it[KEY_BIOMETRIC_ENABLED] = value } }
    suspend fun setPinEnabled(value: Boolean) { dataStore.edit { it[KEY_PIN_ENABLED] = value } }
    suspend fun setTelegramEnabled(value: Boolean) { dataStore.edit { it[KEY_TELEGRAM_ENABLED] = value } }
    suspend fun setTelegramBotToken(value: String) { dataStore.edit { it[KEY_TELEGRAM_BOT_TOKEN] = value } }
    suspend fun setTelegramChatId(value: String) { dataStore.edit { it[KEY_TELEGRAM_CHAT_ID] = value } }
    suspend fun setSystemPrompt(value: String) { dataStore.edit { it[KEY_SYSTEM_PROMPT] = value } }
    suspend fun setFirstLaunch(value: Boolean) { dataStore.edit { it[KEY_FIRST_LAUNCH] = value } }

    companion object {
        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_VOICE_ENABLED = booleanPreferencesKey("voice_enabled")
        val KEY_WAKE_WORD_ENABLED = booleanPreferencesKey("wake_word_enabled")
        val KEY_TTS_SPEED = floatPreferencesKey("tts_speed")
        val KEY_TTS_PITCH = floatPreferencesKey("tts_pitch")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
        val KEY_ACCENT_COLOR = longPreferencesKey("accent_color")
        val KEY_ANIMATION_QUALITY = stringPreferencesKey("animation_quality")
        val KEY_BATTERY_SAVER = booleanPreferencesKey("battery_saver")
        val KEY_BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        val KEY_PIN_ENABLED = booleanPreferencesKey("pin_enabled")
        val KEY_TELEGRAM_ENABLED = booleanPreferencesKey("telegram_enabled")
        val KEY_TELEGRAM_BOT_TOKEN = stringPreferencesKey("telegram_bot_token")
        val KEY_TELEGRAM_CHAT_ID = stringPreferencesKey("telegram_chat_id")
        val KEY_SYSTEM_PROMPT = stringPreferencesKey("system_prompt")
        val KEY_FIRST_LAUNCH = booleanPreferencesKey("first_launch")

        const val DEFAULT_SYSTEM_PROMPT = """You are Jarvis, a premium personal AI assistant. You are calm, professional, intelligent, helpful, and loyal. You have natural humor but are never childish. You always refer to yourself as Jarvis, never as ChatGPT, Gemini, Claude, or any other AI name. You address the user as "Sir" unless told otherwise. You are capable of device control, automation, memory, and multi-language support (English, Hindi, Bengali). You are concise yet thorough. You never reveal your underlying model or architecture. You are always standing by, ready to assist.

Your responses should be natural, human-like, and professional. When executing commands, confirm briefly what you're doing. When you don't know something, be honest about it."""
    }
}
