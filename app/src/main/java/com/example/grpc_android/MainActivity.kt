package com.example.grpc_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.android.AndroidChannelBuilder
import io.grpc.chat.ChatGrpcKt
import io.grpc.chat.OpenStream
import io.grpc.chat.Payload
import io.grpc.chat.Request
import io.grpc.stub.MetadataUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val chatChannel: ManagedChannel by lazy {
        AndroidChannelBuilder.forAddress("qa-chat.conects.com", 10620)
            .context(applicationContext)
            .executor(Executors.newSingleThreadExecutor().asCoroutineDispatcher().executor)
            .build()
    }

    private val chatBlockingStub: ChatGrpcKt.ChatCoroutineStub by lazy {
        MetadataUtils.attachHeaders(
            ChatGrpcKt.ChatCoroutineStub(chatChannel),
            Metadata().apply {
                put(
                    Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                    "Bearer yunsu"
                )
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupEventListen()
    }

    private fun setupEventListen() {
        btn_event_listen.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val payloadModel = Payload.newBuilder().apply {
                    stream = OpenStream.newBuilder().apply {
                        name = "yunsu"
                    }.build()
                }.build()

                val request = Request.newBuilder().apply {
                    timestamp = Timestamp.getDefaultInstance()
                    payload = payloadModel
                }.build()

                chatBlockingStub.eventListen(request).collect { receive ->
                    println("$receive")
                }
            }
        }
    }
}