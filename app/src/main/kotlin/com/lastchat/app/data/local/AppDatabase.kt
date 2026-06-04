package com.lastchat.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lastchat.app.data.local.dao.AssistantDao
import com.lastchat.app.data.local.dao.ConversationDao
import com.lastchat.app.data.local.dao.MessageDao
import com.lastchat.app.data.local.dao.ProviderConfigDao
import com.lastchat.app.data.local.entity.AssistantEntity
import com.lastchat.app.data.local.entity.ConversationEntity
import com.lastchat.app.data.local.entity.MessageEntity
import com.lastchat.app.data.local.entity.ProviderConfigEntity

@Database(
    entities = [
        ConversationEntity::class,
        MessageEntity::class,
        AssistantEntity::class,
        ProviderConfigEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun assistantDao(): AssistantDao
    abstract fun providerConfigDao(): ProviderConfigDao

    companion object {
        const val DATABASE_NAME = "lastchat.db"
    }
}
