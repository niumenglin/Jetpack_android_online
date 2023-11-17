package com.niu.jetpack_android_online.list

import android.content.res.ColorStateList
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.coroutineScope
import androidx.paging.PagingDataAdapter
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.databinding.LayoutFeedAuthorBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedInteractionBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedLabelBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedTextBinding
import com.niu.jetpack_android_online.databinding.LayoutFeedTopCommentBinding
import com.niu.jetpack_android_online.exoplayer.WrapperPlayerView
import com.niu.jetpack_android_online.ext.load
import com.niu.jetpack_android_online.ext.setIconResource
import com.niu.jetpack_android_online.ext.setImageResource
import com.niu.jetpack_android_online.ext.setImageUrl
import com.niu.jetpack_android_online.ext.setTextVisibility
import com.niu.jetpack_android_online.ext.setVisibility
import com.niu.jetpack_android_online.model.Author
import com.niu.jetpack_android_online.model.Feed
import com.niu.jetpack_android_online.model.TYPE_IMAGE_TEXT
import com.niu.jetpack_android_online.model.TYPE_VIDEO
import com.niu.jetpack_android_online.model.TopComment
import com.niu.jetpack_android_online.model.Ugc
import com.niu.jetpack_android_online.utils.PixUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FeedAdapter constructor(private val lifecycle: Lifecycle) :
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
        holder.bindFeedContent(feedItem.feedsText)
        if (feedItem.itemType != TYPE_VIDEO) {//图文类型
            holder.bindFeedImage(
                feedItem.width,
                feedItem.height,
                PixUtil.dp2px(300),
                feedItem.cover
            )
        } else {
            holder.bindVideoData(
                feedItem.width,
                feedItem.height,
                PixUtil.dp2px(300),
                feedItem.cover,
                feedItem.url
            )
        }

        holder.bindLabel(feedItem.activityText)
        holder.bindTopComment(feedItem.topComment)
        holder.bindInteraction(feedItem.ugc)
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
        private val authorBinding =
            LayoutFeedAuthorBinding.bind(itemView.findViewById(R.id.feed_author))
        private val feedTextBinding =
            LayoutFeedTextBinding.bind(itemView.findViewById(R.id.feed_text))
        private val feedImage: ImageView? = itemView.findViewById(R.id.feed_image)
        private val feedLabelBinding =
            LayoutFeedLabelBinding.bind(itemView.findViewById(R.id.feed_label))

        private val feedTopCommentBinding =
            LayoutFeedTopCommentBinding.bind(itemView.findViewById(R.id.feed_top_comment))
        private val feedInteractionBinding =
            LayoutFeedInteractionBinding.bind(itemView.findViewById(R.id.feed_interaction))
        private val playerView: WrapperPlayerView? = itemView.findViewById(R.id.feed_video)

        fun bindAuthor(author: Author?) {
            author?.run {
                authorBinding.authorAvatar.setImageUrl(avatar, true)
                authorBinding.authorName.text = name
            }
        }

        fun bindFeedContent(feedsText: String?) {
            feedTextBinding.root.setTextVisibility(feedsText)
        }

        /**
         * maxHeight：图片最大高度(高>宽)
         * 图片宽度<高度的时候,居中展示，并且填充与图片相近的颜色
         * 图片宽度>高度的时候，宽度填满，高度自适应
         */
        fun bindFeedImage(width: Int, height: Int, maxHeight: Int, cover: String?) {
            if (feedImage == null || TextUtils.isEmpty(cover)) {
                feedImage?.visibility = View.GONE
                return
            }
            val feedItem = getItem(layoutPosition) ?: return
            feedImage.visibility = View.VISIBLE
            feedImage.load(cover!!) {
                //服务器下发图片的宽高都是0
                if (width <= 0 && height <= 0) {
                    setFeedImageSize(it.width, it.height, maxHeight)
                }
                if (feedItem.backgroundColor == 0) {
                    //lifecycle 启动协程，lifecycle所在页面销毁后，协程也相应地自动销毁，不会造成内存泄露！！！
                    lifecycle.coroutineScope.launch(Dispatchers.IO) {
                        //Dispatchers.IO：generate()耗时操作 需要在子线程中执行
                        val defaultColor = feedImage.context.getColor(R.color.color_theme_10)
                        val color = Palette.Builder(it).generate().getMutedColor(defaultColor)
                        feedItem.backgroundColor = color

                        //启动协程，将lifecycle的协程上下文传递进去，切换到主线程。
                        withContext(lifecycle.coroutineScope.coroutineContext) {
                            feedImage.background = ColorDrawable(feedItem.backgroundColor)
                        }
                    }

                }
                feedImage.background = ColorDrawable(feedItem.backgroundColor)
            }

            if (width > 0 && height > 0) {
                setFeedImageSize(width, height, maxHeight)
            }

        }

        private fun setFeedImageSize(width: Int, height: Int, maxHeight: Int) {
            val finalWidth: Int = PixUtil.getScreenWidth()
            val finalHeight: Int =
                if (width > height) (height / (width * 1.0f / finalWidth)).toInt() else maxHeight
            val params = feedImage!!.layoutParams as LinearLayout.LayoutParams
//            params.width = if (width < height) width else finalWidth
            params.width = finalWidth
            params.height = finalHeight
            params.gravity = Gravity.CENTER
            feedImage.scaleType = ImageView.ScaleType.FIT_CENTER
            feedImage.layoutParams = params
        }

        fun bindLabel(activityText: String?) {
            feedLabelBinding.root.setTextVisibility(activityText)
        }

        //神评区
        fun bindTopComment(topComment: TopComment?) {
            feedTopCommentBinding.root.setVisibility(topComment != null)
            feedTopCommentBinding.mediaLayout.setVisibility(topComment?.imageUrl != null)
            topComment?.run {
                feedTopCommentBinding.commentAuthor.setTextVisibility(author?.name)
                feedTopCommentBinding.commentAvatar.setImageUrl(author?.avatar, true)
                feedTopCommentBinding.commentText.setTextVisibility(commentText)
                feedTopCommentBinding.commentLikeCount.setTextVisibility(commentCount.toString())
                feedTopCommentBinding.commentPreviewVideoPlay.setVisibility(videoUrl != null)
                feedTopCommentBinding.commentLikeStatus.setImageResource(
                    hasLiked,
                    R.drawable.icon_cell_liked,
                    R.drawable.icon_cell_like
                )
            }
        }

        fun bindInteraction(ugc: Ugc?) {
            ugc?.run {
                val context = itemView.context
                feedInteractionBinding.interactionLike.text = likeCount.toString()
                feedInteractionBinding.interactionLike.setIconResource(
                    hasLiked,
                    R.drawable.icon_cell_liked,
                    R.drawable.icon_cell_like
                )

                val likeStateColor =
                    ColorStateList.valueOf(context.getColor(if (hasLiked) R.color.color_theme_10 else R.color.color_3d3))
                feedInteractionBinding.interactionLike.iconTint = likeStateColor
                feedInteractionBinding.interactionLike.setTextColor(likeStateColor)

                feedInteractionBinding.interactionDiss.setIconResource(
                    hasdiss,
                    R.drawable.icon_cell_dissed,
                    R.drawable.icon_cell_diss
                )

                val dissStateColor =
                    ColorStateList.valueOf(context.getColor(if (hasdiss) R.color.color_theme_10 else R.color.color_3d3))
                feedInteractionBinding.interactionDiss.iconTint = dissStateColor
                feedInteractionBinding.interactionDiss.setTextColor(dissStateColor)

                feedInteractionBinding.interactionComment.text = commentCount.toString()
                feedInteractionBinding.interactionShare.text = shareCount.toString()
            }
        }

        fun bindVideoData(width: Int, height: Int, maxHeight: Int, cover: String?, url: String?) {
            url?.run {
                playerView?.run {
                    setVisibility(true)
                    //widthPx: Int, heightPx: Int, coverUrl: String?, videoUrl: String, maxHeight: Int
                    bindData(width,height,cover,url,maxHeight)
                }
            }
        }

    }
}