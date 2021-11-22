package com.example.fakeapi.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.fakeapi.retrofit.Post

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postsDao(): PostsDao
}