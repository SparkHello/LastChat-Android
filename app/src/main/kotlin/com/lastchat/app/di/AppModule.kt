package com.lastchat.app.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.lastchat.app.data.local.AppDatabase
import com.lastchat.app.data.repository.AssistantRepository
import com.lastchat.app.data.repository.ChatRepository
import com.lastchat.app.data.repository.ProviderRepository
import com.lastchat.app.data.repository.SettingsRepository
import com.lastchat.app.service.LLMService
import com.lastchat.app.ui.screens.assistant.AssistantViewModel
import com.lastchat.app.ui.screens.chat.ChatViewModel
import com.lastchat.app.ui.screens.conversationlist.ConversationListViewModel
import com.lastchat.app.ui.screens.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

val appModule = module {

    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().conversationDao() }
    single { get<AppDatabase>().messageDao() }
    single { get<AppDatabase>().assistantDao() }
    single { get<AppDatabase>().providerConfigDao() }

    // DataStore
    single { androidContext().dataStore }

    // Services
    single { LLMService() }

    // Repositories
    single { ChatRepository(get(), get()) }
    single { AssistantRepository(get()) }
    single { ProviderRepository(get()) }
    single { SettingsRepository(androidContext()) }

    // ViewModels
    viewModel { (conversationId: String?) ->
        ChatViewModel(get(), get(), get(), conversationId)
    }
    viewModel { ConversationListViewModel(get()) }
    viewModel { AssistantViewModel(get()) }
    viewModel { SettingsViewModel(get(), get()) }
}
