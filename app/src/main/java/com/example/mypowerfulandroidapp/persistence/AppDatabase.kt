package com.example.mypowerfulandroidapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.models.BlogPost

@Database(
    entities = [AccountProperties::class, AuthToken::class, BlogPost::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao
    abstract fun getBlogPostDao(): BlogPostDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}