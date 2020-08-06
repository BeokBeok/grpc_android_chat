package com.example.grpc_android.util

import androidx.room.TypeConverter

object Converter {

    @TypeConverter
    fun fromListOfStrings(listOfString: List<String>): String = listOfString.joinToString()

    @TypeConverter
    fun toListOfString(flatStringList: String): List<String> = flatStringList.split(", ")

}
