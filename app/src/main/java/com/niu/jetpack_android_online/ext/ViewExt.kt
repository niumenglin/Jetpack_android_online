package com.niu.jetpack_android_online.ext

import android.view.View

fun View.setVisibility(condition: Boolean){
    this.visibility = if (condition) View.VISIBLE else View.GONE
}