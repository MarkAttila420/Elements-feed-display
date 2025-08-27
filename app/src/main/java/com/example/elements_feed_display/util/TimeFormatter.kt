package com.example.elements_feed_display.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeFormatter {
    fun formatTime(timeInMillis: Long): String{
        val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val date = Date(timeInMillis)
        return formatter.format(date)
    }
}