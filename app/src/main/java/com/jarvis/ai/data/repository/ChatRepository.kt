package com.jarvis.ai.data.repository

import com.jarvis.ai.data.db.dao.ConversationDao
import com.jarvis.ai.data.db.entity.ConversationEntity
import com.jarvis.ai.data.db.entity.MessageEntity
import com.jarvis.ai.data.preferences.JarvisPreferences
import com.jarvis.ai.data.remote.ApiClient
import com.jarvis.ai.data.remote.ChatMessage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val conversationDao: ConversationDao,
    private val apiClient: ApiClient,
    private val preferences: JarvisPreferences
) {
    fun getAllConversations(): Flow<List<ConversationEntity>> =
        conversationDao.getAllConversations()

    fun getActiveConversation(): Flow<ConversationEntity?> =
        conversationDao.getActiveConversation()

    fun getMessages(conversationId: String): Flow<List<MessageEntity>> =
        conversationDao.getMessagesForConversation(conversationId)

    suspend fun createConversation(title: String = "New Conversation"): String {
        val conversation = ConversationEntity(title = title)
        conversationDao.insertConversation(conversation)
        return conversation.id
    }

    suspend fun getOrCreateActiveConversation(): String {
        val active = conversationDao.getActiveConversation()
        return active?.id ?: createConversation()
    }

    suspend fun addUserMessage(conversationId: String, content: String) {
        conversationDao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = "user",
                content = content
            )
        )
    }

    suspend fun addAssistantMessage(conversationId: String, content: String) {
        conversationDao.insertMessage(
            MessageEntity(
                conversationId = conversationId,
                role = "assistant",
                content = content
            )
        )
    }

    suspend fun getAiResponse(userMessage: String, conversationId: String): Result<String> {
        val messages = conversationDao.getRecentMessages(conversationId, limit = 20)
        val chatMessages = messages.map { ChatMessage(role = it.role, content = it.content) }
        val systemPrompt = preferences.systemPrompt
        // Get active provider would come from AiProviderRepository
        return apiClient.sendChatCompletion(
            baseUrl = "https://openrouter.ai/api/v1",
            apiKey = "",
            model = "openai/gpt-4",
            messages = chatMessages,
            systemPrompt = systemPrompt
        )
    }

    suspend fun deleteConversation(conversationId: String) {
        conversationDao.deleteAllMessages(conversationId)
    }
}
