package com.example.mypowerfulandroidapp.ui.main.create_blog.state

import android.net.Uri

data class CreateBlogViewState(
    //CreateBlogFragment vars
    var blogFields: NewBlogFields = NewBlogFields()
) {
    data class NewBlogFields(
        var title: String? = null,
        var body: String? = null,
        var image: Uri? = null
    )
}