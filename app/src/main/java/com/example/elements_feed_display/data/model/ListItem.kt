package com.example.elements_feed_display.data.model

import android.graphics.Bitmap

sealed class ListItem {
    data class Command(val command: String) : ListItem()
    data class Feed(val content: String, val time: Long) : ListItem()

    data class FeedImage(val image: Bitmap?, val content: String, val time: Long) : ListItem()
}