package com.example.grpc_android.di

import android.app.Application
import dagger.Module
import dagger.Provides
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.android.AndroidChannelBuilder
import io.grpc.chat.ChatGrpcKt
import io.grpc.stub.MetadataUtils
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

    // TODO 동적 Header 설정
    @Provides
    @Singleton
    fun providesChatService(channel: ManagedChannel): ChatGrpcKt.ChatCoroutineStub =
        MetadataUtils.attachHeaders(
            ChatGrpcKt.ChatCoroutineStub(channel),
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer test"
                )
            }
        )

    companion object {
        private const val URL = "qa-chat.conects.com"
        private const val PORT = 10620
    }
}