package com.example.grpc_android.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClass

class BaseSingleAdapter<ITEM : Any>(
    @LayoutRes private val layoutId: Int,
    private val itemBindingId: Int,
    private val viewModels: Map<Int, ViewModel>
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val items = mutableListOf<ITEM>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        BaseViewHolder(parent, layoutId, itemBindingId)

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        holder.bindViewHolder(items[position], viewModels)

    fun replaceItem(newItems: List<ITEM>) {
        items.run {
            clear()
            addAll(newItems)
        }
    }
}

class BaseViewTypeAdapter<ITEM : Any>(
    private val viewHolderMapper: (Any) -> ViewHolderType,
    private val viewHolderType: KClass<out ViewHolderType>,
    private val viewModels: Map<Int, ViewModel>
) : RecyclerView.Adapter<BaseViewHolder>() {

    private val items = mutableListOf<ITEM>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder =
        viewHolderType.java.enumConstants?.get(viewType)?.run {
            BaseViewHolder(parent, layoutId, itemBindingId)
        } ?: BaseViewHolder(parent, -1, -1)

    override fun getItemViewType(position: Int): Int =
        (viewHolderMapper(items[position]) as Enum<*>).ordinal

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        holder.bindViewHolder(item = items[position], viewModels = viewModels)

    fun replaceItem(newItems: List<ITEM>) {
        items.run {
            clear()
            addAll(newItems)
        }
    }
}

class BaseViewHolder(
    parent: ViewGroup,
    layoutId: Int,
    private val itemBindingId: Int
) : RecyclerView.ViewHolder(
    LayoutInflater
        .from(parent.context)
        .inflate(layoutId, parent, false)
) {
    private val binding: ViewDataBinding = DataBindingUtil.bind(itemView)!!

    fun bindViewHolder(item: Any, viewModels: Map<Int, ViewModel>) {
        binding.setVariable(itemBindingId, item)
        for (key in viewModels.keys) {
            binding.setVariable(key, viewModels[key])
        }
        binding.executePendingBindings()
    }
}

interface ViewHolderType {
    val layoutId: Int
    val itemBindingId: Int
}