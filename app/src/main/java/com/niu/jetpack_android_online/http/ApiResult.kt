package com.niu.jetpack_android_online.http

class ApiResult<T> {
    var status = 0
    var errorMsg: String = ""
    var body: T? = null
}