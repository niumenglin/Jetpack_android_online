package com.niu.jetpack_android_online.http

class ApiResult<T> {
    var status = 0
    val success
        get() = status == 200
    var errorMsg: String = ""
    var body: T? = null
}