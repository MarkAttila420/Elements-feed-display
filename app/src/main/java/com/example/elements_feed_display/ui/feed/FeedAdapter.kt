package com.example.elements_feed_display.ui.feed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.elements_feed_display.R
import com.example.elements_feed_display.data.model.ListItem
import com.example.elements_feed_display.ui.feed.view_holder.CommandViewHolder
import com.example.elements_feed_display.ui.feed.view_holder.FeedImageViewHolder
import com.example.elements_feed_display.ui.feed.view_holder.FeedViewHolder

class FeedAdapter(private var items: List<ListItem>) : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        val currentItems: List<ListItem>
                get() = items
        companion object{
                private const val COMMAND = 0
                private const val FEED = 1
                private const val FEED_IMAGE = 2
        }

        override fun getItemViewType(position: Int): Int {
                return when (items[position]){
                        is ListItem.Command -> COMMAND
                        is ListItem.Feed -> FEED
                        is ListItem.FeedImage -> FEED_IMAGE
                }
        }
        override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
        ): RecyclerView.ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return when (viewType){
                        COMMAND -> {
                                val view = inflater.inflate(R.layout.item_command, parent, false)
                                CommandViewHolder(view)
                        }
                        FEED -> {
                                val view = inflater.inflate(R.layout.item_feed, parent, false)
                                FeedViewHolder(view)
                        }
                        FEED_IMAGE -> {
                                val view = inflater.inflate(R.layout.item_feed_image, parent, false)
                                FeedImageViewHolder(view)
                        }
                        else -> throw IllegalArgumentException("Unknown view type: $viewType")
                }
        }

        override fun onBindViewHolder(
                holder: RecyclerView.ViewHolder,
                position: Int
        ) {
                val item = items[position]
                when (item){
                        is ListItem.Command -> (holder as CommandViewHolder).bind(item)
                        is ListItem.Feed -> (holder as FeedViewHolder).bind(item)
                        is ListItem.FeedImage -> (holder as FeedImageViewHolder).bind(item)
                }
        }

        override fun getItemCount(): Int {
                return items.size
        }

        fun updateItems(newItems: List<ListItem>) {
                this.items = newItems
                notifyDataSetChanged()
        }
}