package com.example.grpc_android.di

import com.example.grpc_android.main.MainActivity
import com.example.grpc_android.main.MainActivityModule
import com.example.grpc_android.room.ChatRoomListActivity
import com.example.grpc_android.room.ChatRoomListModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindsMainActivity(): MainActivity

    @ContributesAndroidInjector(modules = [ChatRoomListModule::class])
    abstract fun bindsChatRoomListActivity(): ChatRoomListActivity
}