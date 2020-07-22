package com.example.grpc_android.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity

abstract class BaseActivity<VDB : ViewDataBinding>(
    @LayoutRes private val layoutRes: Int
) : DaggerAppCompatActivity() {

    protected lateinit var binding: VDB
    open fun setupViewModel() = Unit
    open fun setupObserve() = Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupBinding()
        setupViewModel()
        setupObserve()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, layoutRes)
        binding.lifecycleOwner = this
    }
}