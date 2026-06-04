package com.lastchat.app

import android.app.Application
import com.lastchat.app.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class LastChatApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@LastChatApplication)
            modules(appModule)
        }
    }
}
