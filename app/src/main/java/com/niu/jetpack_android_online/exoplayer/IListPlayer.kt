package com.niu.jetpack_android_online.exoplayer

import android.view.ViewGroup

interface IListPlayer {
    /**
     * 获取当前视频播放器的exoPlayer(textureView)是否已经被挂载在某个容器上
     */
    val attachedView: ViewGroup?

    /**
     * 正在视频播放
     */
    val isPlaying: Boolean

    /**
     * 页面不可见时，暂停播放
     */
    fun inActive()

    /**
     * 页面恢复可见时，继续播放
     */
    fun onActive()

    /**
     * 点击播放/暂停按钮，继续播放
     */
    fun togglePlay(attachView: WrapperPlayerView, videoUrl: String)

    /**
     * 释放视频播放器资源
     */
    fun stop(release: Boolean)
}