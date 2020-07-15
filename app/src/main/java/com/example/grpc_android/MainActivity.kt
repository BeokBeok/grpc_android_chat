package com.example.grpc_android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.protobuf.Timestamp
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.Metadata
import io.grpc.chat.ChatGrpc
import io.grpc.chat.OpenStream
import io.grpc.chat.Payload
import io.grpc.chat.Request
import io.grpc.stub.MetadataUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private val chatChannel: ManagedChannel =
        ManagedChannelBuilder.forAddress("qa-chat.conects.com", 10620)
            .usePlaintext()
            .executor(Executors.newSingleThreadExecutor().asCoroutineDispatcher().executor)
            .build()

    private val chatBlockingStub: ChatGrpc.ChatBlockingStub = MetadataUtils.attachHeaders(
        ChatGrpc.newBlockingStub(chatChannel),
        Metadata().apply {
            put(
                Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER),
                "Bearer test"
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupListener()
    }

    private fun setupListener() {
        setupEventListen()
    }

    private fun setupEventListen() {
        btn_event_listen.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val payloadModel = Payload.newBuilder().apply {
                    stream = OpenStream.newBuilder().apply {
                        name = "test"
                    }.build()
                }.build()

                val request = Request.newBuilder().apply {
                    timestamp = Timestamp.getDefaultInstance()
                    payload = payloadModel
                }.build()

                chatBlockingStub.eventListen(request).forEach { receive ->
                    println("$receive")
                }
            }
        }
    }
}