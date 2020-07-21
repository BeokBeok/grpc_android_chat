package com.example.grpc_android

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import io.grpc.ManagedChannel
import io.grpc.Metadata
import io.grpc.android.AndroidChannelBuilder
import io.grpc.chat.*
import io.grpc.stub.MetadataUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import java.util.concurrent.Executors
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

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

    private val coroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            throwable.printStackTrace()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupEventListen()
        setupChatWith()
        setupChatIn()
//        setupChatOut()
//        setupSendMessage()
//        setupGetMessage()
        setupObserve()
    }

    /**
     * 채팅 관련 각종 이벤트를 받을 수 있는 리스너 등록
     */
    private fun setupEventListen() {
        btn_event_listen.setOnClickListener {
            viewModel.eventListen(tiet_uid.text.toString())
        }
    }

    /**
     * 채팅방 생성
     */
    private fun setupChatWith() {
        btn_chat_with.setOnClickListener {
            viewModel.chatWith(tiet_uid.text.toString(), "test1")
        }
    }

    /**
     * 채팅방 진입
     */
    private fun setupChatIn() {
        btn_chat_in.setOnClickListener {
            viewModel.chatIn(tiet_uid.text.toString(), tiet_cid.text.toString())
        }
    }

    /**
     * 채팅방 퇴장
     */
    private fun setupChatOut() {
        btn_chat_out.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val chatOutModel = ChatOut.newBuilder().apply {
                    uid = tiet_uid.text.toString()
                    lastMsgLid = "0"
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    chatOut = chatOutModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = tiet_cid.text.toString()
                    pldType = PayloadType.CHATOUT
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.chatOut(request) }
                println("chatOut is ${result.await().resp}")
            }
        }
    }

    /**
     * 메시지 전송
     */
    private fun setupSendMessage() {
        btn_send_message.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val sendMessageModel = Message.newBuilder().apply {
                    uid = tiet_uid.text.toString()
                    message = tiet_message.text.toString()
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    message = sendMessageModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = tiet_cid.text.toString()
                    pldType = PayloadType.MESSAGE
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.sendMessage(request) }
                println("sendMessage is ${result.await()}")
            }
        }
    }

    /**
     * 채팅방 메시지 조회
     */
    private fun setupGetMessage() {
        btn_get_message.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch(coroutineExceptionHandler) {
                val paginationModel = Pagination.newBuilder().apply {
                    pageSize = 30
                    pageToken = 1
                }.build()

                val getMessagesModel = Messages.newBuilder().apply {
                    lastLid = "0"
                    pagination = paginationModel
                }.build()

                val payloadModel = Payload.newBuilder().apply {
                    messages = getMessagesModel
                }.build()

                val request = Request.newBuilder().apply {
                    cid = tiet_cid.text.toString()
                    pldType = PayloadType.GETMESSAGES
                    payload = payloadModel
                }.build()

                val result = async { chatBlockingStub.getMessages(request) }
                println("getMessage is ${result.await()}")
            }
        }
    }

    private fun setupObserve() {
        val owner = this

        viewModel.run {
            receive.observe(owner, Observer {
                println("receive it $it")
            })
            cid.observe(owner, Observer {
                println("cid is $it")
                tiet_cid.setText(it)
            })
            errMsg.observe(owner, Observer {
                println("err is $it")
            })
            output.observe(owner, Observer {
                println("output is $it")
            })
        }
    }
}