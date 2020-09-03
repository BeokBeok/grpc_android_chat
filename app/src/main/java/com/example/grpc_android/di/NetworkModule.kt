package com.example.grpc_android.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.grpc.ManagedChannel
import io.grpc.android.AndroidChannelBuilder
import io.grpc.chat.ChatGrpcKt
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun providesSingleThreadExecutor() =
        Executors.newSingleThreadExecutor().asCoroutineDispatcher().executor

    @Provides
    @Singleton
    fun providesChatChannel(
        applicationContext: Application,
        executor: Executor
    ): ManagedChannel = AndroidChannelBuilder.forAddress(URL, PORT)
        .context(applicationContext)
        .executor(executor)
        .build()

    @Provides
    @Singleton
    fun providesChatService(channel: ManagedChannel): ChatGrpcKt.ChatCoroutineStub =
            ChatGrpcKt.ChatCoroutineStub(channel)

    companion object {
        private const val URL = "url"
        private const val PORT = 0
    }
}
