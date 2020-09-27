package com.example.mypowerfulandroidapp.ui.main.create_blog.state

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
const val CREATE_BLOG_VIEW_STATE_BUNDLE_KEY="com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogViewState"

@Parcelize
data class CreateBlogViewState(
    //CreateBlogFragment vars
    var blogFields: NewBlogFields = NewBlogFields()
) : Parcelable {
    @Parcelize
    data class NewBlogFields(
        var title: String? = null,
        var body: String? = null,
        var image: Uri? = null
    ) : Parcelable
}