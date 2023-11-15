package com.niu.jetpack_android_online.http

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiInterface {

    @GET("feeds/queryHotFeedsList")
    suspend fun getFeeds(
        @Query("feedId") feedId: Int = 0,
        @Query("feedType") feedType: String = "all",
        @Query("pageCount") pageCount: Int = 10,
        @Query("userId") userId: Int = 0
    ): JsonObject

}