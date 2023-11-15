package com.niu.jetpack_android_online.http

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiService {
    private const val BASE_URL = "http://8.136.122.222/jetpack/"
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
//        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(GsonConverterFactory())
        .build()

    fun getService(): IApiInterface {
        return retrofit.create(IApiInterface::class.java)
    }
}