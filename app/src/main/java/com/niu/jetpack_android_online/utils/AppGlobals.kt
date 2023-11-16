package com.niu.jetpack_android_online.utils

import android.app.Application

private var sApplication: Application? = null

object AppGlobals {
    //反射ActivityThread#currentApplication()
    fun getApplication(): Application {
        if (sApplication == null) {
            kotlin.runCatching {
                //className:全类名
                Class.forName("android.app.ActivityThread").getMethod("currentApplication")
                    .invoke(null, *emptyArray()) as Application
            }.onFailure {
                it.printStackTrace()
            }
        }
        return sApplication!!
    }
}