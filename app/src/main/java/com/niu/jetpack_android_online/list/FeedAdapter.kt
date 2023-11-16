package com.niu.jetpack_android_online.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.databinding.LayoutFeedAuthorBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedInteractionBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedTextBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedTopCommentBinding
import com.niu.jetpack_android_online.ext.setImageUrl
import com.niu.jetpack_android_online.model.Author
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
        val feedItem = getItem(position) ?: return
        holder.bindAuthor(feedItem.author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedViewHolder {
        if (viewType != TYPE_IMAGE_TEXT && viewType != TYPE_VIDEO) {
            val view = View(parent.context)
            view.visibility = View.GONE
            return FeedViewHolder(view)
        }
        val layoutResId =
            if (viewType == TYPE_IMAGE_TEXT) R.layout.layout_feed_type_image else R.layout.layout_feed_type_video
        return FeedViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutResId, parent, false)
        )
    }

    inner class FeedViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val authorBinding = LayoutFeedAuthorBinding.bind(itemView.findViewById(R.id.feed_author))
        val feedTextBinding = LayoutFeedTextBinding.bind(itemView.findViewById(R.id.feed_text))
        val feedImage: ImageView? = itemView.findViewById(R.id.feed_image)
        val feedTopCommentBinding =
            LayoutFeedTopCommentBinding.bind(itemView.findViewById(R.id.feed_top_comment))
        val feedInteractionBinding =
            LayoutFeedInteractionBinding.bind(itemView.findViewById(R.id.feed_interaction))

        fun bindAuthor(author: Author?) {
            author?.run {
                authorBinding.authorAvatar.setImageUrl(avatar, true)
                authorBinding.authorName.text = name
            }
        }

    }
}