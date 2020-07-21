package com.example.grpc_android.di

import com.example.grpc_android.data.ChatDataRepository
import com.example.grpc_android.data.ChatRemoteDataSource
import com.example.grpc_android.data.ChatRemoteService
import com.example.grpc_android.data.ChatRepository
import dagger.Module
import dagger.Provides
import io.grpc.chat.ChatGrpcKt
import javax.inject.Singleton

@Module(includes = [NetworkModule::class])
class DataModule {

    @Provides
    @Singleton
    fun providesChatRemoteDataSource(chatService: ChatGrpcKt.ChatCoroutineStub): ChatRemoteService =
        ChatRemoteDataSource(chatService)

    @Provides
    @Singleton
    fun providesChatDataRepository(chatRemoteDataSource: ChatRemoteDataSource): ChatRepository =
        ChatDataRepository(chatRemoteDataSource)
}