package com.niu.jetpack_android_online.pages.login

import android.content.Intent
import com.niu.jetpack_android_online.cache.CacheManager
import com.niu.jetpack_android_online.model.Author
import com.niu.jetpack_android_online.utils.AppGlobals
import kotlinx.coroutines.flow.MutableStateFlow

object UserManager {
    private val userFlow: MutableStateFlow<Author> = MutableStateFlow(Author())

    suspend fun save(author: Author) {
        CacheManager.get().authorDao.save(author)
        userFlow.emit(author)
    }

    fun isLogin(): Boolean {
        return userFlow.value.expiresTime > System.currentTimeMillis()
    }

    fun loginIfNeed() {
        if (isLogin()) {
            return
        }
        val intent = Intent(AppGlobals.getApplication(), LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        AppGlobals.getApplication().startActivity(intent)
    }

    suspend fun getUser(): Author {
        loadCache()
        return userFlow.value
    }

    suspend fun userId():Long{
        loadCache()
        return userFlow.value.userId
    }

    private suspend fun loadCache() {
        if (!isLogin()) {
            val cache = CacheManager.get().authorDao.getUser()
            cache?.run {
                userFlow.emit(this)
            }
        }
    }
}