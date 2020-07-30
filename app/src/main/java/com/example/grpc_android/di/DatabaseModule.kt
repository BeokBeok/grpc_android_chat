package com.example.grpc_android.di

import android.app.Application
import androidx.room.Room
import com.example.grpc_android.data.local.ChatDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Provides
    @Singleton
    fun providesChatDatabase(applicationContext: Application) =
        Room.databaseBuilder(
            applicationContext,
            ChatDatabase::class.java,
            "Chat.db"
        ).build()

    @Provides
    @Singleton
    fun providesChatRoomDao(chatDatabase: ChatDatabase) = chatDatabase.chatRoomDao()
}