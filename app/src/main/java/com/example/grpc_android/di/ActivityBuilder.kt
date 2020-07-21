package com.example.grpc_android.di

import com.example.grpc_android.MainActivity
import com.example.grpc_android.MainActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {

    @ContributesAndroidInjector(modules = [MainActivityModule::class])
    abstract fun bindsMainActivity(): MainActivity
}