package com.example.mypowerfulandroidapp.persistence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mypowerfulandroidapp.models.BlogPost

@Dao
interface BlogPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(blogPost: BlogPost): Long

    @Query("SELECT * FROM blog_post")
    fun getAllBlogPosts():LiveData<List<BlogPost>>
}