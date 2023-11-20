package com.niu.jetpack_android_online.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

const val TYPE_TEXT = 0 //文本类型帖子
const val TYPE_IMAGE_TEXT = 1 //图文类型帖子
const val TYPE_VIDEO = 2//视频类型帖子

@Keep
data class Feed(
    val activityIcon: String?,
    val activityText: String?,
    val author: Author?,
    val authorId: Long,
    val cover: String?,
    val createTime: Long,
    val duration: Double,
    val feedsText: String?,
    val height: Int,
    val id: Long,
    val itemId: Long,
    val itemType: Int,
    val topComment: TopComment?,
    val ugc: Ugc?,
    val url: String?,
    val width: Int
) {
    var backgroundColor: Int = 0
}

@Entity(tableName = "author")
@Keep
data class Author(
    val avatar: String,
    val commentCount: Int,
    val description: String?,
    val expiresTime: Int,
    val favoriteCount: Int,
    val feedCount: Int,
    val followCount: Int,
    val followerCount: Int,
    val hasFollow: Boolean,
    val historyCount: Int,
    val likeCount: Int,
    val name: String,
    val qqOpenId: String,
    val score: Int,
    @ColumnInfo(name = "topCount", defaultValue = "0")
    val topCount: Int,
    @PrimaryKey(autoGenerate = false)
    val userId: Long
) {
    constructor() : this("", 0, "", 0, 0, 0, 0, 0, false, 0, 0, "", "", 0, 0, 0)
}

@Keep
data class TopComment(
    val author: Author?,
    val commentId: Long,
    val commentText: String,//评论的正文
    val commentCount: Int,//评论数
    val commentType: Int,
    val commentUgc: Ugc?,
    val createTime: Long,
    val hasLiked: Boolean,
    val height: Int,
    val id: Int,
    val imageUrl: String?,
    val itemId: Long,
    val userId: Long,
    val videoUrl: String?,
    val width: Int
)

@Keep
data class Ugc(
    val commentCount: Int,
    val hasFavorite: Boolean,
    val hasLiked: Boolean,
    val hasdiss: Boolean,
    val itemId: Long,
    val likeCount: Int,
    val shareCount: Int
)