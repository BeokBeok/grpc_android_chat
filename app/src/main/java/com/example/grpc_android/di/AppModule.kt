package com.example.grpc_android.di

import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.util.ChatEventReceiver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun providesChatEventReceiver(chatRepository: ChatRepository) =
        ChatEventReceiver(chatRepository)
}