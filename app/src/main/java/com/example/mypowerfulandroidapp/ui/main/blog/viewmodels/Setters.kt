package com.example.mypowerfulandroidapp.ui.main.blog.viewmodels

import android.net.Uri
import com.example.mypowerfulandroidapp.models.BlogPost

fun BlogViewModel.setQuery(query: String) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.searchQuery = query
    setViewState(update)
}

fun BlogViewModel.setBlogListData(blogList: List<BlogPost>) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.blogList = blogList
    setViewState(update)
}

fun BlogViewModel.setBlogPost(blogPost: BlogPost) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.blogPost = blogPost
    setViewState(update)
}

fun BlogViewModel.setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
    setViewState(update)
}

fun BlogViewModel.setQueryExhausted(isExhausted: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryExhausted = isExhausted
    setViewState(update)
}

fun BlogViewModel.setQueryInProgress(isInProgress: Boolean) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.isQueryInProgress = isInProgress
    setViewState(update)
}

fun BlogViewModel.setBlogPostsOrder(order: String) {
    val update = getCurrentViewStateOrNew()
    update.blogFields.order = order
    setViewState(update)
}

fun BlogViewModel.setBlogPostsFilter(filter: String?) {
    filter?.let {
        val update = getCurrentViewStateOrNew()
        update.blogFields.filter = it
        setViewState(update)
    }
}

fun BlogViewModel.removeDeletedBlogPost() {
    val update = getCurrentViewStateOrNew()
    val list = update.blogFields.blogList.toMutableList()
    for (i in 0..(list.size - 1)) {
        if (list[i] == getBlogPost()) {
            list.remove(getBlogPost())
            break
        }
    }
    setBlogListData(list)
}


fun BlogViewModel.setUpdatedBlogFields(
    title: String?,
    body: String?,
    uri: Uri?
) {
    val update = getCurrentViewStateOrNew()
    val newUpdatedBlogFields = update.updateBlogFields
    title?.let { newUpdatedBlogFields.updateBlogTitle = it }
    body?.let { newUpdatedBlogFields.updateBlogBody = it }
    uri?.let { newUpdatedBlogFields.updateBlogImage = it }
    update.updateBlogFields = newUpdatedBlogFields
    setViewState(update)
}