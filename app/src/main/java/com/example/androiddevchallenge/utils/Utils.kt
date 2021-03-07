package com.example.androiddevchallenge.utils

import java.text.SimpleDateFormat
import java.util.*

object Utils {

    fun formattingTimeStamp(timestamp: Long, pattern: String = "hh:mm:ss"): String {
        val dateFormat = SimpleDateFormat(pattern, Locale.ENGLISH)
        return dateFormat.format(timestamp)
    }
}