package com.jarvis.ai.data.remote

import com.jarvis.ai.data.preferences.JarvisPreferences
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

/**
 * API Client for communicating with AI providers.
 * Supports OpenRouter, Ollama, and custom OpenAI-compatible APIs.
 */
class ApiClient(private val preferences: JarvisPreferences) {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Send a chat completion request (non-streaming).
     */
    suspend fun sendChatCompletion(
        baseUrl: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        temperature: Float = 0.7f,
        topP: Float = 0.95f,
        maxTokens: Int = 4096,
        systemPrompt: String? = null
    ): Result<String> {
        return try {
            val allMessages = buildList {
                if (!systemPrompt.isNullOrBlank()) {
                    add(ChatMessage(role = "system", content = systemPrompt))
                }
                addAll(messages)
            }

            val requestBody = ChatCompletionRequest(
                model = model,
                messages = allMessages,
                temperature = temperature,
                topP = topP,
                maxTokens = maxTokens,
                stream = false
            )

            val body = json.encodeToString(
                ChatCompletionRequest.serializer(),
                requestBody
            ).toRequestBody("application/json".toMediaType())

            val url = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "$baseUrl/chat/completions"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .apply {
                    if (apiKey.isNotBlank()) {
                        addHeader("Authorization", "Bearer $apiKey")
                    }
                    addHeader("Content-Type", "application/json")
                }
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseBody = response.body?.string() ?: ""
                val parsed = json.decodeFromString(ChatCompletionResponse.serializer(), responseBody)
                val content = parsed.choices.firstOrNull()?.message?.content ?: ""
                Result.success(content)
            } else {
                Result.failure(Exception("API error: ${response.code} - ${response.body?.string()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Send a streaming chat completion request using SSE.
     */
    fun sendStreamingCompletion(
        baseUrl: String,
        apiKey: String,
        model: String,
        messages: List<ChatMessage>,
        temperature: Float = 0.7f,
        topP: Float = 0.95f,
        maxTokens: Int = 4096,
        systemPrompt: String? = null,
        onToken: (String) -> Unit,
        onComplete: () -> Unit,
        onError: (Throwable) -> Unit
    ): EventSource? {
        return try {
            val allMessages = buildList {
                if (!systemPrompt.isNullOrBlank()) {
                    add(ChatMessage(role = "system", content = systemPrompt))
                }
                addAll(messages)
            }

            val requestBody = ChatCompletionRequest(
                model = model,
                messages = allMessages,
                temperature = temperature,
                topP = topP,
                maxTokens = maxTokens,
                stream = true
            )

            val body = json.encodeToString(
                ChatCompletionRequest.serializer(),
                requestBody
            ).toRequestBody("application/json".toMediaType())

            val url = if (baseUrl.endsWith("/")) "${baseUrl}chat/completions" else "$baseUrl/chat/completions"

            val request = Request.Builder()
                .url(url)
                .post(body)
                .apply {
                    if (apiKey.isNotBlank()) {
                        addHeader("Authorization", "Bearer $apiKey")
                    }
                    addHeader("Content-Type", "application/json")
                    addHeader("Accept", "text/event-stream")
                }
                .build()

            val factory = EventSources.createFactory(httpClient)
            factory.newEventSource(request, object : EventSourceListener() {
                override fun onEvent(eventSource: EventSource, id: String?, type: String?, data: String) {
                    if (data == "[DONE]") {
                        onComplete()
                        return
                    }
                    try {
                        val parsed = json.decodeFromString(StreamingChatResponse.serializer(), data)
                        val content = parsed.choices.firstOrNull()?.delta?.content
                        if (!content.isNullOrBlank()) {
                            onToken(content)
                        }
                    } catch (_: Exception) {
                        // Skip malformed chunks
                    }
                }

                override fun onFailure(eventSource: EventSource, t: Throwable?, response: okhttp3.Response?) {
                    t?.let { onError(it) } ?: onError(Exception("Stream failed"))
                }

                override fun onClosed(eventSource: EventSource) {
                    onComplete()
                }
            })
        } catch (e: Exception) {
            onError(e)
            null
        }
    }

    /**
     * Test connection to an AI provider.
     */
    suspend fun testConnection(
        baseUrl: String,
        apiKey: String,
        model: String
    ): Result<String> {
        return sendChatCompletion(
            baseUrl = baseUrl,
            apiKey = apiKey,
            model = model,
            messages = listOf(ChatMessage(role = "user", content = "Hello, respond with just 'OK'")),
            maxTokens = 10
        )
    }
}

// ---- Request / Response data classes ----

@Serializable
data class ChatMessage(
    val role: String,
    val content: String
)

@Serializable
data class ChatCompletionRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val temperature: Float = 0.7f,
    @SerialName("top_p") val topP: Float = 0.95f,
    @SerialName("max_tokens") val maxTokens: Int = 4096,
    val stream: Boolean = false
)

@Serializable
data class ChatCompletionResponse(
    val id: String? = null,
    val choices: List<Choice> = emptyList(),
    val usage: Usage? = null
) {
    @Serializable
    data class Choice(
        val index: Int = 0,
        val message: ChatMessage,
        val finishReason: String? = null
    )

    @Serializable
    data class Usage(
        val promptTokens: Int = 0,
        val completionTokens: Int = 0,
        val totalTokens: Int = 0
    )
}

@Serializable
data class StreamingChatResponse(
    val id: String? = null,
    val choices: List<StreamChoice> = emptyList()
) {
    @Serializable
    data class StreamChoice(
        val index: Int = 0,
        val delta: StreamDelta,
        val finishReason: String? = null
    )
}

@Serializable
data class StreamDelta(
    val role: String? = null,
    val content: String? = null
)
