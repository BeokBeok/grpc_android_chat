package com.example.grpc_android.util

import java.text.SimpleDateFormat
import java.util.*

object TimeConverter {

    private const val A_H_MM = "a h:mm"
    private val ahmmDateFormat = SimpleDateFormat(A_H_MM, Locale.KOREA)

    fun lidToTime(lid: String): String =
        ahmmDateFormat.format(lid.substring(1..16).toInt(16))
}