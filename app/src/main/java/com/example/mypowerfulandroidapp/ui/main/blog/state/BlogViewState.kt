package com.example.mypowerfulandroidapp.ui.main.blog.state

import com.example.mypowerfulandroidapp.models.BlogPost

data class BlogViewState(
    //blogFragment vars
    var blogFields: BlogFields = BlogFields()
    //viewBlogFragment var

    //update blog fragment var
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = ""
    )
}