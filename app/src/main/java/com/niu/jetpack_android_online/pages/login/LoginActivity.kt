package com.niu.jetpack_android_online.pages.login

import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.niu.jetpack_android_online.App
import com.niu.jetpack_android_online.base.BaseActivity
import com.niu.jetpack_android_online.databinding.ActivityLayoutLoginBinding
import com.niu.jetpack_android_online.http.ApiService
import com.niu.jetpack_android_online.utils.showToast
import com.tencent.connect.UserInfo
import com.tencent.connect.common.Constants
import com.tencent.tauth.IUiListener
import com.tencent.tauth.Tencent
import com.tencent.tauth.UiError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginActivity : BaseActivity<ActivityLayoutLoginBinding>() {

    private lateinit var tencent: Tencent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.actionClose.setOnClickListener { finish() }
        binding.actionLogin.setOnClickListener { login() }

        tencent = Tencent.createInstance(App.TENCENT_APP_ID, applicationContext)
    }

    private fun login() {
        tencent.login(this, "all", loginListener)
    }

    private val loginListener = object : LoginListener() {
        override fun onComplete(ret: Any) {
            val response = ret as JSONObject
            val openId = response.optString("openid")
            val accessToken = response.optString("access_token")
            val expiresIn = response.optString("expires_in")
            tencent.openId = openId
            tencent.setAccessToken(accessToken, expiresIn.toString())
            getUserInfo()
        }
    }

    private fun getUserInfo() {
        val userInfo = UserInfo(applicationContext, tencent.qqToken)
        userInfo.getUserInfo(object : LoginListener() {
            override fun onComplete(any: Any) {
                super.onComplete(any)
                val response = any as JSONObject
                val nickname = response.optString("nickname")
                val avatar =
                    response.optString("figureurl_2")//figureurl_1:50x50 figureurl_2:100x100
                //将用户信息保存至服务器
                save(nickname, avatar)
            }
        })
    }

    private fun save(nickname: String, avatar: String) {
        lifecycleScope.launch {
            val apiResult = ApiService.getService()
                .saveUser(nickname, avatar, tencent.openId, tencent.expiresIn)
            if (apiResult.success && apiResult.body != null) {
                //关闭当前登录页面
                UserManager.save(apiResult.body!!)
                finish()
            } else {
                //切换到主线程
                withContext(Dispatchers.Main) {
                    "登录失败".showToast()
                }
            }
        }
    }

    private open inner class LoginListener : IUiListener {
        override fun onComplete(p0: Any) {
        }

        override fun onError(err: UiError) {
            "登录失败:reason${err.errorMessage}".showToast()
        }

        override fun onCancel() {
            "登录失败".showToast()
        }

        override fun onWarning(p0: Int) {
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, loginListener)
        }
    }
}