package com.example.grpc_android

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : DaggerAppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<MainViewModel> { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupEventListen()
        setupChatWith()
        setupChatIn()
        setupChatOut()
        setupSendMessage()
        setupGetMessage()
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
            viewModel.chatOut(tiet_uid.text.toString(), tiet_cid.text.toString())
        }
    }

    /**
     * 메시지 전송
     */
    private fun setupSendMessage() {
        btn_send_message.setOnClickListener {
            viewModel.sendMessage(
                tiet_uid.text.toString(),
                tiet_cid.text.toString(),
                tiet_message.text.toString()
            )
        }
    }

    /**
     * 채팅방 메시지 조회
     */
    private fun setupGetMessage() {
        btn_get_message.setOnClickListener {
            viewModel.getMessages(tiet_cid.text.toString())
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
                Toast.makeText(owner, it, Toast.LENGTH_SHORT).show()
            })
            output.observe(owner, Observer {
                println("output is $it")
            })
        }
    }
}