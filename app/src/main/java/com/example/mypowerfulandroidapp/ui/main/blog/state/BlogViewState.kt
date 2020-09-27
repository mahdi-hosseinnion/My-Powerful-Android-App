package com.example.mypowerfulandroidapp.ui.main.blog.state

import android.net.Uri
import android.os.Parcelable
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.persistence.BlogQueryUtils
import kotlinx.android.parcel.Parcelize
const val BLOG_VIEW_STATE_BUNDLE_KEY="com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState"
@Parcelize
data class BlogViewState(
    //blogFragment vars
    var blogFields: BlogFields = BlogFields(),
    //ViewBlogFragment var
    var viewBlogFields: ViewBlogFields = ViewBlogFields(),
    //update blog fragment var
    var updateBlogFields: UpdateBlogFields = UpdateBlogFields()
) : Parcelable {
    @Parcelize
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = "",
        var page: Int = 1,
        var isQueryInProgress: Boolean = false,
        var isQueryExhausted: Boolean = false,
        var filter: String = BlogQueryUtils.BLOG_FILTER_DATE_UPDATED,
        var order: String = BlogQueryUtils.BLOG_ORDER_ASC
    ) : Parcelable

    @Parcelize
    data class ViewBlogFields(
        var blogPost: BlogPost? = null,
        var isAuthorOfBlogPost: Boolean = false
    ) : Parcelable

    @Parcelize
    data class UpdateBlogFields(
        var updateBlogTitle: String? = null,
        var updateBlogBody: String? = null,
        var updateBlogImage: Uri? = null
    ) : Parcelable
}