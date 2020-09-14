package com.example.mypowerfulandroidapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mypowerfulandroidapp.api.main.OpenApiMainService
import com.example.mypowerfulandroidapp.api.main.responses.BlogListSearchResponse
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.persistence.BlogPostDao
import com.example.mypowerfulandroidapp.persistence.returnOrderedBlogQuery
import com.example.mypowerfulandroidapp.repository.JobManager
import com.example.mypowerfulandroidapp.repository.NetworkBoundResource
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.example.mypowerfulandroidapp.util.DateUtils
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.IO_PARALLELISM_PROPERTY_NAME
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("BlogRepository") {

    private val TAG = "BlogRepository"

    fun searchBlogPosts(
        authToken: AuthToken,
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            false,
            true
        ) {

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main) {
                    //finishing by viewing the db cache
                    result.addSource(loadFromCache()) { blogViewState ->
                        blogViewState.blogFields.isQueryInProgress = false
                        if ((page * PAGINATION_PAGE_SIZE) > blogViewState.blogFields.blogList.size) {
                            blogViewState.blogFields.isQueryExhausted = true
                        }
                        onCompleteJob(
                            DataState.data(blogViewState, null)
                        )

                    }

                }
            }

            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<BlogListSearchResponse>) {
                val blogPostList = ArrayList<BlogPost>()
                if (apiSuccessResponse.body.results.isEmpty())
                    return
                for (blogListSearch in apiSuccessResponse.body.results) {
                    blogPostList.add(
                        BlogPost(
                            pk = blogListSearch.pk,
                            title = blogListSearch.title,
                            body = blogListSearch.body,
                            image = blogListSearch.image,
                            slug = blogListSearch.slug,
                            username = blogListSearch.username,
                            date_updated = DateUtils.convertServerStringDateToLong(
                                blogListSearch.date_updated
                            )
                        )
                    )
                }

                updateLocalDb(blogPostList)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return openApiMainService.searchListBlogPosts(
                    "Token ${authToken.token}",
                    query,
                    filterAndOrder,
                    page
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return blogPostDao.returnOrderedBlogQuery(
                    query,
                    filterAndOrder,
                    page
                )
                    .switchMap {
                        object : LiveData<BlogViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = BlogViewState(
                                    blogFields = BlogViewState.BlogFields(
                                        it,
                                        isQueryInProgress = true
                                    )
                                )
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: List<BlogPost>?) {
                if (cacheObject != null) {
                    withContext(IO) {
                        for (blogPost in cacheObject) {
                            try {
                                //launch each insert as separated job to execute in parallel
                                launch {
                                    Log.d(TAG, "updateLocalDb: inserting the $blogPost into cache")
                                    blogPostDao.insertOrReplace(blogPost)
                                }
                            } catch (e: Exception) {
                                Log.e(
                                    TAG,
                                    "updateLocalDb: error while update ${blogPost.title} with slug: ${blogPost.slug}",
                                    e
                                )
                            }
                        }
                    }
                }
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

        }.getAsLiveData()
    }
}