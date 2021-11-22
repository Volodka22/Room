package com.example.fakeapi.database

import androidx.room.*
import com.example.fakeapi.retrofit.Post

@Dao
interface PostsDao {
    @Query("SELECT * FROM post ORDER BY id DESC")
    suspend fun getAll(): List<Post>

    @Insert
    suspend fun insert(post: Post)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg posts: Post)

    @Delete
    suspend fun delete(post: Post)

}