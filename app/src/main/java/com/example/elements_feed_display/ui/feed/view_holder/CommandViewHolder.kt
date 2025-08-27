package com.example.elements_feed_display.ui.feed.view_holder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.elements_feed_display.R
import com.example.elements_feed_display.data.model.ListItem

class CommandViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(commandItem: ListItem.Command) {
        itemView.findViewById<TextView>(R.id.TvCommand).text = commandItem.command
    }
}