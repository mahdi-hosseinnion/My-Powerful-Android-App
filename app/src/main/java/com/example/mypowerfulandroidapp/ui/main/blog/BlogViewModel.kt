package com.example.mypowerfulandroidapp.ui.main.blog

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.repository.main.BlogRepository
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.BaseViewModel
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    val blogRepository: BlogRepository,
    val sharedPreferences: SharedPreferences,
    val requestManager: RequestManager,
    val sessionManager: SessionManager
) : BaseViewModel<BlogStateEvent, BlogViewState>() {


    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken,
                        viewState.value!!.blogFields.searchQuery
                    )
                } ?: AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                AbsentLiveData.create()
            }
            is BlogStateEvent.None ->
                AbsentLiveData.create()
        }
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
    }

    fun setBlogPost(blogPost: BlogPost) {
        val update = getCurrentViewStateOrNew()
        update.viewBlogFields.blogPost = blogPost
        _ViewState.value = update
    }

    fun setIsAuthorOfBlogPost(isAuthorOfBlogPost: Boolean) {
        val update = getCurrentViewStateOrNew()
        update.viewBlogFields.isAuthorOfBlogPost = isAuthorOfBlogPost
        _ViewState.value = update
    }

    fun setQuery(query: String) {
        val update = getCurrentViewStateOrNew()
//        if (update.blogFields.searchQuery == query) {
//            return
//        }
        update.blogFields.searchQuery = query
        _ViewState.value = update

    }

    fun setBlogListData(blogList: List<BlogPost>) {
        val update = getCurrentViewStateOrNew()
        update.blogFields.blogList = blogList
        _ViewState.value = update
    }

    fun cancelActiveJobs() {
        blogRepository.cancelActiveJobs()
        handlePendingData()
    }

    private fun handlePendingData() {
        setStatEvent(BlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }


}