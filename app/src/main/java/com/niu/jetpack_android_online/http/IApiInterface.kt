package com.niu.jetpack_android_online.http

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.niu.jetpack_android_online.model.Author
import com.niu.jetpack_android_online.model.Feed
import retrofit2.http.GET
import retrofit2.http.Query

interface IApiInterface {

    /**
     * 查看帖子列表
     * @param feedId 帖子id，分页时传列表最后一个帖子的id
     * @param feedType 帖子类型， all：全部类型； pics：仅图片类型；video：仅视频类型；text：仅文本类型
     * @param pageCount 分页数量
     * @param userId 当前登录者的id
     */
    @GET("feeds/queryHotFeedsList")
    suspend fun getFeeds(
        @Query("feedId") feedId: Long = 0,
        @Query("feedType") feedType: String = "all",
        @Query("pageCount") pageCount: Int = 10,
        @Query("userId") userId: Int = 0
    ): ApiResult<List<Feed>>

    /**
     * 创建或更新一个新的用户
     * @param name 用户名
     * @param avatar 用户头像
     * @param qqOpenId qq登录后获得，代表用户的唯一身份
     * @param expires_time 登录过期时间
     */
    @GET("user/insert")
    suspend fun saveUser(
        @Query("name") name: String,
        @Query("avatar") avatar: String,
        @Query("qq0openId") qqOpenId: String,
        @Query("expires_time") expires_time: Long
    ): ApiResult<Author>

}