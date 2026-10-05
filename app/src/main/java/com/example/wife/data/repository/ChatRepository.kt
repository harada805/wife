package com.example.wife.data.repository

import com.example.wife.data.local.dao.ChatDao
import com.example.wife.data.local.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val chatDao: ChatDao
) {
    fun getAllMessages(): Flow<List<ChatMessageEntity>> = chatDao.getAllMessages()

    suspend fun getRecentMessages(limit: Int): List<ChatMessageEntity> =
        chatDao.getRecentMessages(limit)

    suspend fun sendMessage(text: String, isFromUser: Boolean, toolCallJson: String? = null): Long {
        val message = ChatMessageEntity(
            text = text,
            isFromUser = isFromUser,
            toolCallJson = toolCallJson
        )
        return chatDao.insertMessage(message)
    }

    suspend fun deleteMessage(message: ChatMessageEntity) {
        chatDao.deleteMessage(message)
    }

    suspend fun clearHistory() {
        chatDao.clearAll()
    }
}
