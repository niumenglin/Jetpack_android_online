package com.niu.jetpack_android_online.ext

import android.content.Intent
import com.niu.jetpack_android_online.utils.AppGlobals

/**
 * 启动Activity
 * 泛型实化+高阶函数
 * 使用案例
 * startActivity<LoginActivity> {
 *   addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
 * }
 */
inline fun <reified T> startActivity(block: Intent.() -> Unit) {
    val app = AppGlobals.getApplication()
    val intent = Intent(app, T::class.java)
    intent.block()
    app.startActivity(intent)
}