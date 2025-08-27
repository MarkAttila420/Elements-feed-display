package com.example.elements_feed_display.ui.feed.view_holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elements_feed_display.R
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.util.TimeFormatter

class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(feedItem: ListItem.Feed) {
        val timeString = TimeFormatter.formatTime(feedItem.time)
        itemView.findViewById<TextView>(R.id.TvFeedTime).text = timeString
        itemView.findViewById<TextView>(R.id.TvFeedContent).text = feedItem.content
    }
}