package com.example.grpc_android.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object TimeConverter {

    private const val A_H_MM = "a h:mm"
    private const val YYYY_MM_DD_EEEE = "yyyy-MM-dd EEEE"

    @SuppressLint("SimpleDateFormat")
    private val ahmmDateFormat = SimpleDateFormat(A_H_MM)

    @SuppressLint("SimpleDateFormat")
    private val yyyymmddeeeDateFormat = SimpleDateFormat(YYYY_MM_DD_EEEE)

    private val TIMESTAMP_RANGE = 1..16

    fun lidToHourMinute(lid: String): String = ahmmDateFormat.format(Date(lidToTimestamp(lid)))

    fun lidToYearMonthDay(lid: String): String =
        yyyymmddeeeDateFormat.format(Date(lidToTimestamp(lid)))

    private fun lidToTimestamp(lid: String) =
        lid.substring(TIMESTAMP_RANGE).toLong(16) / 1_000 * 1_000
}