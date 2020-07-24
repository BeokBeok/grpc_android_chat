package com.example.grpc_android.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeConverter {

    private const val A_H_MM = "a h:mm"

    @SuppressLint("SimpleDateFormat")
    private val ahmmDateFormat = SimpleDateFormat(A_H_MM)

    private const val YYYY_MM_DD_EEEE = "yyyy-MM-dd EEEE"

    @SuppressLint("SimpleDateFormat")
    private val yyyymmddeeeDateFormat = SimpleDateFormat(YYYY_MM_DD_EEEE)


    fun lidToHourMinute(lid: String): String =
        ahmmDateFormat.format(Date(lid.substring(1..16).toLong(16) * 1_000))

    fun lidToYearMonthDay(lid: String): String =
        yyyymmddeeeDateFormat.format(Date(lid.substring(1..16).toLong(16) * 1_000))
}