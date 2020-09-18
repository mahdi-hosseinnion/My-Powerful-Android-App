package com.example.mypowerfulandroidapp.ui.main.blog.state

import android.net.Uri
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.persistence.BlogQueryUtils

data class BlogViewState(
    //blogFragment vars
    var blogFields: BlogFields = BlogFields(),
    //ViewBlogFragment var
    var viewBlogFields: ViewBlogFields = ViewBlogFields(),
    //update blog fragment var
    var updateBlogFields: UpdateBlogFields = UpdateBlogFields()
) {
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = BlogQueryUtils.BLOG_FILTER_DATE_UPDATED,
        var order: String = BlogQueryUtils.BLOG_ORDER_ASC
    )

    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    )

    data class UpdateBlogFields(
        var updateBlogTitle: String? = null,
        var updateBlogBody: String? = null,
        var updateBlogImage: Uri? = null
    )
}