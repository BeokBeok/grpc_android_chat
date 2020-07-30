package com.example.grpc_android.di

import com.example.grpc_android.data.ChatDataRepository
import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.data.remote.ChatRemoteDataSource
import com.example.grpc_android.data.remote.ChatRemoteService
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