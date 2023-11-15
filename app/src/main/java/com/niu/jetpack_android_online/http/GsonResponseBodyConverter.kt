package com.niu.jetpack_android_online.http

import com.google.gson.Gson
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import java.lang.RuntimeException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class GsonResponseBodyConverter<T> constructor(private val gson: Gson, val type: Type) :
    Converter<ResponseBody, ApiResult<T>> {
    override fun convert(value: ResponseBody): ApiResult<T>? {
        value.use {
            if (type !is ParameterizedType || !ApiResult::class.java.isAssignableFrom((type.rawType) as Class<*>))
                throw RuntimeException("The return type of the method must be ApiResult<*>")

            val apiResult = ApiResult<T>()
            val response = JSONObject(value.string())
            apiResult.status = response.optInt("status")
            apiResult.errorMsg = response.optString("message")
            val data1: JSONObject? = response.optJSONObject("data")
            if (data1 != null) {
                val data2: String? = data1.optString("data")
                if (data2 != null) {
                    val argumentType = type.actualTypeArguments[0]
                    apiResult.body = gson.fromJson(data2, argumentType)
                }
            }
            return apiResult
        }
    }
}