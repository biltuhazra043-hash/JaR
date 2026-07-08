package com.jarvis.ai.telegram

import com.jarvis.ai.data.remote.ChatMessage
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * Telegram Bot integration service.
 * Handles receiving commands and sending notifications via Telegram.
 */
class TelegramBotService(
    private val botToken: String,
    private val chatId: String
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    private val baseUrl = "https://api.telegram.org/bot$botToken"
    private var lastUpdateId = 0L
    private var pollJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    /**
     * Send a message to the configured chat.
     */
    suspend fun sendMessage(text: String): Result<Boolean> {
        return try {
            val body = """
                {"chat_id": "$chatId", "text": "${text.replace("\"", "\\\"").replace("\n", "\\n")}", "parse_mode": "Markdown"}
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/sendMessage")
                .post(body)
                .build()

            val response = httpClient.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send a photo to the configured chat.
     */
    suspend fun sendPhoto(photoUrl: String, caption: String = ""): Result<Boolean> {
        return try {
            val body = """
                {"chat_id": "$chatId", "photo": "$photoUrl", "caption": "${caption.replace("\"", "\\\"")}", "parse_mode": "Markdown"}
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("$baseUrl/sendPhoto")
                .post(body)
                .build()

            val response = httpClient.newCall(request).execute()
            Result.success(response.isSuccessful)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Start polling for updates.
     */
    fun startPolling(onMessage: (String) -> Unit) {
        pollJob?.cancel()
        pollJob = scope.launch {
            while (isActive) {
                try {
                    val updates = getUpdates()
                    for (update in updates) {
                        val text = update.message?.text ?: continue
                        if (text.isNotBlank()) {
                            onMessage(text)
                        }
                        lastUpdateId = (update.updateId + 1).toLong()
                    }
                } catch (_: Exception) {
                    delay(5000) // Retry after error
                }
                delay(1000) // Poll interval
            }
        }
    }

    fun stopPolling() {
        pollJob?.cancel()
    }

    private suspend fun getUpdates(): List<TelegramUpdate> {
        return try {
            val request = Request.Builder()
                .url("$baseUrl/getUpdates?offset=$lastUpdateId&timeout=30")
                .get()
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val body = response.body?.string() ?: return emptyList()
                val parsed = json.decodeFromString(TelegramGetUpdatesResponse.serializer(), body)
                parsed.result ?: emptyList()
            } else emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Serializable
data class TelegramGetUpdatesResponse(
    val ok: Boolean = false,
    val result: List<TelegramUpdate>? = null
)

@Serializable
data class TelegramUpdate(
    val updateId: Long = 0,
    val message: TelegramMessage? = null
)

@Serializable
data class TelegramMessage(
    val messageId: Long = 0,
    val text: String? = null,
    val from: TelegramUser? = null
)

@Serializable
data class TelegramUser(
    val id: Long = 0,
    val firstName: String = "",
    val username: String = ""
)
