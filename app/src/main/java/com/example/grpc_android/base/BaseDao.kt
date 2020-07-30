package com.example.grpc_android.base

import androidx.room.Insert

interface BaseDao<T> {
    @Insert
    fun insert(vararg obj: T)
}