package com.niu.jetpack_android_online.utils

import android.widget.Toast
import com.niu.jetpack_android_online.App

fun String.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.context, this, duration).show()
}

fun Int.showToast(duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(App.context, this, duration).show()
}