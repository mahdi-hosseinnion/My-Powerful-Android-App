package com.example.mypowerfulandroidapp.ui.main.blog.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.persistence.BlogQueryUtils
import com.example.mypowerfulandroidapp.repository.main.BlogRepository
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.BaseViewModel
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import com.example.mypowerfulandroidapp.util.PreferenceKeys.Companion.BLOG_FILTER
import com.example.mypowerfulandroidapp.util.PreferenceKeys.Companion.BLOG_ORDER
import javax.inject.Inject

class BlogViewModel
@Inject
constructor(
    val blogRepository: BlogRepository,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val editor: SharedPreferences.Editor
) : BaseViewModel<BlogStateEvent, BlogViewState>() {

    init {
        setBlogPostsFilter(
            sharedPreferences.getString(BLOG_FILTER, BlogQueryUtils.BLOG_FILTER_DATE_UPDATED)
                ?: BlogQueryUtils.BLOG_FILTER_DATE_UPDATED

        )
        setBlogPostsOrder(
            sharedPreferences.getString(BLOG_ORDER, BlogQueryUtils.BLOG_ORDER_ASC)
                ?: BlogQueryUtils.BLOG_ORDER_ASC
        )
    }

    override fun handleStateEvent(stateEvent: BlogStateEvent): LiveData<DataState<BlogViewState>> {
        return when (stateEvent) {
            is BlogStateEvent.BlogSearchEvent -> {
                sessionManager.cachedToken.value?.let { authToken ->
                    blogRepository.searchBlogPosts(
                        authToken = authToken,
                        query = getSearchQuery(),
                        filterAndOrder = getOrder() + getFilter(),
                        page = getPage()
                    )
                } ?: AbsentLiveData.create()
            }
            is BlogStateEvent.CheckAuthorOfBlogPost -> {
                AbsentLiveData.create()
            }
            is BlogStateEvent.None ->
                object : LiveData<DataState<BlogViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        value = DataState.loading(
                            false,
                            null
                        )
                    }
                }
        }
    }

    fun saveFilterOptions(filter: String, order: String) {
        editor.putString(BLOG_FILTER, filter)
        editor.apply()
        editor.putString(BLOG_ORDER, order)
        editor.apply()
    }

    override fun initNewViewState(): BlogViewState {
        return BlogViewState()
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