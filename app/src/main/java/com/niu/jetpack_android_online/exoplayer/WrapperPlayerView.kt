package com.niu.jetpack_android_online.exoplayer

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.exoplayer2.Player
import com.niu.jetpack_android_online.R
import com.niu.jetpack_android_online.databinding.LayoutListWrapperPlayerViewBinding
import com.niu.jetpack_android_online.ext.setBlurImageUrl
import com.niu.jetpack_android_online.ext.setImageUrl
import com.niu.jetpack_android_online.ext.setVisibility
import com.niu.jetpack_android_online.utils.PixUtil

/**
 * 用于动态挂载 视频播放控制器 和 显示视频画面的playerView
 */
class WrapperPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : FrameLayout(context, attrs) {
    internal var videoHeightPx: Int = 0
    internal var videoWidthPx: Int = 0
    private var callback: Listener? = null
    private val viewBinding =
        LayoutListWrapperPlayerViewBinding.inflate(LayoutInflater.from(context), this)

    init {
        viewBinding.playBtn.setOnClickListener {
            callback?.onTogglePlay(this)
        }
    }

    fun bindData(widthPx: Int, heightPx: Int, coverUrl: String?, videoUrl: String, maxHeight: Int) {
        //1、根据视频的原始宽widthPx，高heightPx 动态计算出cover blur 以及wrapperView的宽高
        viewBinding.cover.setImageUrl(coverUrl)
        if (widthPx < heightPx) {
            coverUrl?.run {
                viewBinding.blurBackground.setVisibility(true)
                viewBinding.blurBackground.setBlurImageUrl(this, 10)
            }
        } else {
            viewBinding.blurBackground.setVisibility(false)
        }

        this.videoWidthPx = widthPx
        this.videoHeightPx = heightPx
        setSize(widthPx, heightPx, PixUtil.getScreenWidth(), maxHeight)
    }

    private fun setSize(widthPx: Int, heightPx: Int, maxWidth: Int, maxHeight: Int) {
        //计算视频原始宽度>原始高度时 or 原始高度>原始宽度  cover、wrapperView等比缩放
        val coverWidth: Int
        val coverHeight: Int
        if (widthPx >= heightPx) {
            coverWidth = maxWidth
            coverHeight = (heightPx / (widthPx * 1.0f / maxWidth)).toInt()
        } else {
            coverHeight = maxHeight
            coverWidth = (widthPx / (heightPx * 1.0f / maxHeight)).toInt()
        }

        //设置wrapper-view的宽高
        val wrapperParams = layoutParams
        wrapperParams.width = maxWidth //屏幕宽度
        wrapperParams.height = coverHeight //封面高度
        layoutParams = wrapperParams

        //设置高斯模糊背景view的宽高
        val blurParams = viewBinding.blurBackground.layoutParams
        blurParams.width = maxWidth //屏幕宽度
        blurParams.height = coverHeight //封面高度
        viewBinding.blurBackground.layoutParams = blurParams

        //设置cover-view封面图的宽高
        val coverParams: LayoutParams = viewBinding.cover.layoutParams as LayoutParams
        coverParams.width = coverWidth
        coverParams.height = coverHeight
        coverParams.gravity = Gravity.CENTER
        viewBinding.cover.scaleType = ImageView.ScaleType.FIT_CENTER
        viewBinding.cover.layoutParams = coverParams
    }

    fun onActive(playerView: View, controllerView: View) {
        val parent = playerView.parent
        if (parent != this) {
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
            }
            val coverParams = viewBinding.cover.layoutParams
            this.addView(playerView, 1, coverParams)
        }

        val ctrlParent = controllerView.parent
        if (ctrlParent != this) {
            if (ctrlParent != null) {
                (ctrlParent as ViewGroup).removeView(controllerView)
            }
            val ctrlParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
            ctrlParams.gravity = Gravity.BOTTOM
            this.addView(controllerView, ctrlParams)
        }
    }

    //暂停播放
    fun inActive() {
        viewBinding.cover.setVisibility(true)
        viewBinding.playBtn.setVisibility(true)
        viewBinding.playBtn.setImageResource(R.drawable.icon_video_play)
    }

    //根据播放事件，更新 WrapperPlayerView
    fun onPlayerStateChanged(playing: Boolean, playbackState: Int) {
        if (playing) {//正在播放
            viewBinding.cover.setVisibility(false)
            viewBinding.bufferView.setVisibility(false)
            viewBinding.playBtn.setVisibility(true)
            viewBinding.playBtn.setImageResource(R.drawable.icon_video_pause)
        } else if (playbackState == Player.STATE_ENDED) {//播放完成
            viewBinding.cover.setVisibility(true)
            viewBinding.playBtn.setVisibility(true)
            viewBinding.playBtn.setImageResource(R.drawable.icon_video_play)
        } else if (playbackState == Player.STATE_BUFFERING) {//缓冲加载中
            viewBinding.bufferView.setVisibility(true)
        }
    }

    fun onControllerVisibilityChange(visibility: Int, payEnd: Boolean) {
        viewBinding.playBtn.setVisibility(if (payEnd) true else visibility == View.VISIBLE)
    }

    fun setListener(callback: Listener) {
        this.callback = callback
    }

    interface Listener {
        fun onTogglePlay(attachView: WrapperPlayerView)
    }

}