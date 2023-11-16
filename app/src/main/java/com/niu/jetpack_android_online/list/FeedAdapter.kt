package com.niu.jetpack_android_online.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.model.Feed
import com.niu.jetpack_android_online.model.TYPE_IMAGE_TEXT
import com.niu.jetpack_android_online.model.TYPE_VIDEO

class FeedAdapter :
    PagingDataAdapter<Feed, FeedAdapter.FeedViewHolder>(object : DiffUtil.ItemCallback<Feed>() {
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }
    }) {

    override fun getItemViewType(position: Int): Int {
        val feedItem = getItem(position) ?: return 0
        return feedItem.itemType
    }

    override fun onBindViewHolder(holder: FeedViewHolder, position: Int) {
        val feedItem = getItem(position)?:return
        val feedText = holder.itemView.findViewById<TextView>(R.id.feed_text)
        feedText.text = feedItem.feedsText
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        if (viewType!= TYPE_IMAGE_TEXT&&viewType!= TYPE_VIDEO){
            val view = View(parent.context)
            view.visibility = View.GONE
            return FeedViewHolder(view)
        }
        val layoutResId = if (viewType == TYPE_IMAGE_TEXT) R.layout.layout_feed_type_image else R.layout.layout_feed_type_video
        return FeedViewHolder(LayoutInflater.from(parent.context).inflate(layoutResId,parent,false))
    }

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }
}