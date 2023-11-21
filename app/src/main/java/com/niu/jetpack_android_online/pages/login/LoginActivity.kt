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

    private val loginListener = object : LoginListener() {//{"ret":0,"openid":"9BDB399D6F72F5C1A0AF7E3278F96DD9","access_token":"329E434DE7636796EBED75EF6F3E2AE0","pay_token":"74EBB72B781D2A06C404046691060BF5","expires_in":7776000,"pf":"desktop_m_qq-10000144-android-2002-","pfkey":"e9963856eddcb97e2cb528d2016b520b","msg":"","login_cost":66,"query_authority_cost":0,"authority_cost":0,"expires_time":1708320776286}
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
            override fun onComplete(any: Any) {//{"ret":0,"msg":"","is_lost":0,"nickname":"林枫","gender":"男","gender_type":2,"province":"广东","city":"深圳","year":"1990","constellation":"","figureurl":"http:\/\/qzapp.qlogo.cn\/qzapp\/102075085\/9BDB399D6F72F5C1A0AF7E3278F96DD9\/30","figureurl_1":"http:\/\/qzapp.qlogo.cn\/qzapp\/102075085\/9BDB399D6F72F5C1A0AF7E3278F96DD9\/50","figureurl_2":"http:\/\/qzapp.qlogo.cn\/qzapp\/102075085\/9BDB399D6F72F5C1A0AF7E3278F96DD9\/100","figureurl_qq_1":"http:\/\/thirdqq.qlogo.cn\/g?b=oidb&k=gicK89sMSConnYxiaZHjMv6A&kti=ZVxBUAAAAAE&s=40&t=1555473694","figureurl_qq_2":"http:\/\/thirdqq.qlogo.cn\/g?b=oidb&k=gicK89sMSConnYxiaZHjMv6A&kti=ZVxBUAAAAAE&s=100&t=1555473694","figureurl_qq":"http:\/\/thirdqq.qlogo.cn\/g?b=oidb&k=gicK89sMSConnYxiaZHjMv6A&kti=ZVxBUAAAAAE&s=640&t=1555473694","figureurl_type":"1","is_yellow_vip":"0","vip":"0","yellow_vip_level":"0","level":"0","is_yellow_year_vip":"0"}
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