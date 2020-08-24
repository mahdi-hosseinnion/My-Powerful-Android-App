package com.example.mypowerfulandroidapp.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.models.AuthToken

@Database(
    entities = [AccountProperties::class, AuthToken::class],
    version = 2
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getAuthTokenDao(): AuthTokenDao
    abstract fun getAccountPropertiesDao(): AccountPropertiesDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}