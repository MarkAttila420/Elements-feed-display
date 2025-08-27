package com.example.elements_feed_display.ui.feed.view_holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elements_feed_display.R
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.util.TimeFormatter

class FeedImageViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(feedItem: ListItem.FeedImage) {
        val timeString = TimeFormatter.formatTime(feedItem.time)
        itemView.findViewById<TextView>(R.id.TvFeedImageTime).text = timeString
        itemView.findViewById<TextView>(R.id.TvFeedImageContent).text = feedItem.content
        itemView.findViewById<ImageView>(R.id.IvFeedImage).setImageBitmap(feedItem.image)
    }
}