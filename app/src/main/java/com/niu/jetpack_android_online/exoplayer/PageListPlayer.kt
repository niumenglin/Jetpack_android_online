package com.niu.jetpack_android_online.exoplayer

import android.view.ViewGroup

class PageListPlayer : IListPlayer {
    override val attachedView: ViewGroup?
        get() = null
    override val isPlaying: Boolean
        get() = false

    override fun inActive() {

    }

    override fun onActive() {

    }

    override fun togglePlay(attachView: WrapperPlayerView, videoUrl: String) {

    }

    override fun stop(release: Boolean) {

    }

    companion object {
        private val sPageListPlayers = hashMapOf<String, IListPlayer>()
        fun get(pageName: String): IListPlayer {
            var pageListPlayer = sPageListPlayers[pageName]
            if (pageListPlayer == null) {
                pageListPlayer = PageListPlayer()
                sPageListPlayers[pageName] = pageListPlayer
            }
            return pageListPlayer
        }

        fun stop(pageName: String, release: Boolean = true) {
            if (release) {
                sPageListPlayers[pageName]?.stop(true)
            } else {
                sPageListPlayers[pageName]?.stop(false)
            }
        }
    }
}