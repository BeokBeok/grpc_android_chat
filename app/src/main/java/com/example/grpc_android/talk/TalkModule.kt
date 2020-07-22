package com.example.grpc_android.talk

import androidx.lifecycle.ViewModel
import com.example.grpc_android.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class TalkModule {

    @Binds
    @IntoMap
    @ViewModelKey(TalkViewModel::class)
    abstract fun bindsTalkViewModel(viewModel: TalkViewModel): ViewModel
}