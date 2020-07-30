package com.example.grpc_android.di

import com.example.grpc_android.data.ChatDataRepository
import com.example.grpc_android.data.ChatRepository
import com.example.grpc_android.data.local.ChatLocalDataSource
import com.example.grpc_android.data.local.ChatLocalService
import com.example.grpc_android.data.local.ChatRoomDao
import com.example.grpc_android.data.remote.ChatRemoteDataSource
import com.example.grpc_android.data.remote.ChatRemoteService
import dagger.Module
import dagger.Provides
import io.grpc.chat.ChatGrpcKt
import javax.inject.Singleton

@Module(includes = [DatabaseModule::class, NetworkModule::class])
class DataModule {

    @Provides
    @Singleton
    fun providesChatLocalDataSource(chatRoomDao: ChatRoomDao): ChatLocalService =
        ChatLocalDataSource(chatRoomDao)

    @Provides
    @Singleton
    fun providesChatRemoteDataSource(chatService: ChatGrpcKt.ChatCoroutineStub): ChatRemoteService =
        ChatRemoteDataSource(chatService)

    @Provides
    @Singleton
    fun providesChatDataRepository(chatRemoteDataSource: ChatRemoteDataSource): ChatRepository =
        ChatDataRepository(chatRemoteDataSource)
}