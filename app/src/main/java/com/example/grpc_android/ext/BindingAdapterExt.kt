package com.example.grpc_android.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.grpc_android.base.BaseSingleAdapter

@BindingAdapter("bind:replaceItem")
fun replaceItem(recyclerView: RecyclerView, items: List<Any>?) {
    if (items == null) return

    @Suppress("UNCHECKED_CAST")
    (recyclerView.adapter as? BaseSingleAdapter<Any>)?.run {
        replaceItem(items)
        notifyDataSetChanged()
    }
}