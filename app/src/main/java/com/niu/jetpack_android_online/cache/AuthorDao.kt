package com.niu.jetpack_android_online.cache

import androidx.room.*
import com.niu.jetpack_android_online.model.Author

//data access object
@Dao
interface AuthorDao {

    /**
     * 增
     * return  success>-1; fail<0
     * 根据表中主键判断是否冲突
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(author: Author): Long

    /**
     * 删
     * success >-1;  fail=-1
     */
    @Delete
    suspend fun delete(author: Author): Int

    /**
     * 查
     */
    @Query("select * from author limit 1")
    suspend fun getUser(): Author?

    /**
     * 改
     * success >-1;  fail=-1
     */
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(author: Author): Int

}