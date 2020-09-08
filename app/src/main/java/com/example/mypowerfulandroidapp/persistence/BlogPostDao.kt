package com.example.mypowerfulandroidapp.persistence

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.mypowerfulandroidapp.models.BlogPost

@Dao
interface BlogPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(blogPost: BlogPost): Long
}