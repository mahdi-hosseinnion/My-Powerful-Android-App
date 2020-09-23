package com.example.mypowerfulandroidapp.ui.main.create_blog.state

import okhttp3.MultipartBody

sealed class CreateBlogStateEvent {
    data class CreateNewBlogPostEvent(
        val title: String,
        val body: String,
        val image: MultipartBody.Part
    ) : CreateBlogStateEvent()

    class None() : CreateBlogStateEvent()
}