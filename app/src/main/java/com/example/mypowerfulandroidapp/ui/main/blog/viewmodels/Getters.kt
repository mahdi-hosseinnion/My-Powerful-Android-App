package com.example.mypowerfulandroidapp.ui.main.blog.viewmodels

fun BlogViewModel.getSearchQuery(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.searchQuery
    }
}

fun BlogViewModel.getPage(): Int {
    getCurrentViewStateOrNew().let {
        return it.blogFields.page
    }
}

fun BlogViewModel.getQueryExhausted(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.blogFields.isQueryExhausted
    }
}

fun BlogViewModel.getQueryInProgress(): Boolean {
    getCurrentViewStateOrNew().let {
        return it.blogFields.isQueryInProgress
    }
}

fun BlogViewModel.getOrder(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.order
    }
}

fun BlogViewModel.getFilter(): String {
    getCurrentViewStateOrNew().let {
        return it.blogFields.filter
    }
}

fun BlogViewModel.getSlug(): String {
    getCurrentViewStateOrNew().let {
        it.viewBlogFields.blogPost?.let { blogPost ->
            return blogPost.slug
        }
    }
    return ""

}
fun BlogViewModel.isAuthorOfBlogPost():Boolean{
    getCurrentViewStateOrNew().let {
        return it.viewBlogFields.isAuthorOfBlogPost
    }
}
































