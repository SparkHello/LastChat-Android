package com.lastchat.app.data.repository

import com.lastchat.app.data.local.dao.ConversationDao
import com.lastchat.app.data.local.dao.MessageDao
import com.lastchat.app.data.model.Conversation
import com.lastchat.app.data.model.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatRepository(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao
) {
    fun getAllConversations(): Flow<List<Conversation>> =
        conversationDao.getAll().map { list ->
            list.map { Conversation.fromEntity(it) }
        }

    suspend fun getConversationById(id: String): Conversation? =
        conversationDao.getById(id)?.let { Conversation.fromEntity(it) }

    suspend fun createConversation(conversation: Conversation) {
        conversationDao.insert(conversation.toEntity())
    }

    suspend fun updateConversation(conversation: Conversation) {
        conversationDao.update(conversation.toEntity())
    }

    suspend fun updateTitle(conversationId: String, title: String) {
        conversationDao.updateTitle(conversationId, title)
    }

    suspend fun deleteConversation(conversation: Conversation) {
        conversationDao.delete(conversation.toEntity())
    }

    suspend fun deleteConversationById(id: String) {
        conversationDao.deleteById(id)
    }

    fun getMessagesForConversation(conversationId: String): Flow<List<Message>> =
        messageDao.getMessagesForConversation(conversationId).map { list ->
            list.map { Message.fromEntity(it) }
        }

    suspend fun sendMessage(message: Message) {
        messageDao.insert(message.toEntity())
        conversationDao.incrementMessageCount(message.conversationId)
    }

    suspend fun sendMessages(messages: List<Message>) {
        messageDao.insertAll(messages.map { it.toEntity() })
        messages.firstOrNull()?.let {
            conversationDao.incrementMessageCount(it.conversationId)
        }
    }

    suspend fun deleteMessagesForConversation(conversationId: String) {
        messageDao.deleteMessagesForConversation(conversationId)
    }

    suspend fun createConversationWithMessage(
        title: String,
        message: Message,
        assistantId: String? = null
    ): String {
        val conversation = Conversation(
            id = message.conversationId,
            title = title,
            assistantId = assistantId
        )
        conversationDao.insert(conversation.toEntity())
        messageDao.insert(message.toEntity())
        conversationDao.incrementMessageCount(conversation.id)
        return conversation.id
    }
}
