package com.example.grpc_android.room

import androidx.lifecycle.ViewModel
import com.example.grpc_android.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ChatRoomListModule {

    @Binds
    @IntoMap
    @ViewModelKey(ChatRoomListViewModel::class)
    abstract fun bindsChatRoomListViewModel(viewModel: ChatRoomListViewModel): ViewModel
}