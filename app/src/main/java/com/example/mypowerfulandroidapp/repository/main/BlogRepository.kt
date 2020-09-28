package com.example.mypowerfulandroidapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mypowerfulandroidapp.api.GenericResponse
import com.example.mypowerfulandroidapp.api.main.OpenApiMainService
import com.example.mypowerfulandroidapp.api.main.responses.BlogCreateUpdateResponse
import com.example.mypowerfulandroidapp.api.main.responses.BlogListSearchResponse
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.persistence.BlogPostDao
import com.example.mypowerfulandroidapp.persistence.returnOrderedBlogQuery
import com.example.mypowerfulandroidapp.repository.JobManager
import com.example.mypowerfulandroidapp.repository.NetworkBoundResource
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.Response
import com.example.mypowerfulandroidapp.ui.ResponseType
import com.example.mypowerfulandroidapp.ui.main.blog.state.BlogViewState
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.Constants.Companion.PAGINATION_PAGE_SIZE
import com.example.mypowerfulandroidapp.util.DateUtils
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.UNKNOWN_ERROR
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_HAS_PERMISSION_TO_EDIT
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.SUCCESS_BLOG_DELETED
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import kotlin.collections.ArrayList

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

    fun restoreBlogListFromCache(
        query: String,
        filterAndOrder: String,
        page: Int
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogListSearchResponse, List<BlogPost>, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            false,
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
                //ignore
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogListSearchResponse>> {
                return AbsentLiveData.create()

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
                //ignore
            }

            override fun setJob(job: Job) {
                addJob("searchBlogPosts", job)
            }

        }.getAsLiveData()
    }

    fun isAuthorOfBlogPost(
        authToken: AuthToken,
        slug: String
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<GenericResponse, Any, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            //not applicable
            override suspend fun createCacheRequestAndReturn() {}

            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    Log.d(TAG, "handleApiSuccessResponse: ${apiSuccessResponse.body.response}")
                    //TODO(this line should be false :::BUG:::)
                    var isAuthor = false//fixed
                    if (apiSuccessResponse.body.response == RESPONSE_HAS_PERMISSION_TO_EDIT) {
                        isAuthor = true

                    }
                    onCompleteJob(
                        DataState.data(
                            data = BlogViewState(
                                viewBlogFields = BlogViewState.ViewBlogFields(
                                    isAuthorOfBlogPost = isAuthor
                                )
                            )
                        )
                    )

                }
            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.isAuthorOfBlogPost(
                    "Token ${authToken.token}",
                    slug
                )
            }

            //not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            //not applicable
            override suspend fun updateLocalDb(cacheObject: Any?) {}

            override fun setJob(job: Job) {
                addJob("isAuthorOfBlogPost", job)
            }
        }.getAsLiveData()
    }

    fun deleteBlogPost(
        authToken: AuthToken,
        blogPost: BlogPost
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<GenericResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            //not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<GenericResponse>) {
                withContext(Main) {
                    if (apiSuccessResponse.body.response == SUCCESS_BLOG_DELETED) {
                        updateLocalDb(blogPost)
                    } else {
                        onCompleteJob(
                            DataState.error(
                                Response(
                                    UNKNOWN_ERROR,
                                    ResponseType.Dialog()
                                )
                            )
                        )
                    }
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<GenericResponse>> {
                return openApiMainService.deleteBlogPost(
                    "Token ${authToken.token}",
                    blogPost.slug
                )
            }

            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogPostDao.deleteBlogPost(cacheObject)
                    onCompleteJob(
                        DataState.data(
                            data = null,
                            response = Response(
                                SUCCESS_BLOG_DELETED,
                                ResponseType.Toast()
                            )
                        )
                    )
                }

            }

            override fun setJob(job: Job) {
                addJob("deleteBlogPost", job)
            }
        }.getAsLiveData()
    }

    fun updateBlogPost(
        authToken: AuthToken,
        slug: String,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<BlogViewState>> {
        return object : NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, BlogViewState>(
            sessionManager.isConnectedToTheInternet(),
            true,
            true,
            false
        ) {
            //not applicable
            override suspend fun createCacheRequestAndReturn() {

            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {
                Log.e(TAG, "handleApiSuccessResponse: ${response.body.response}")

                if (response.body.title == "null") {

                    val updatedBlogPost = BlogPost(
                        pk = response.body.pk,
                        title = response.body.title,
                        slug = response.body.slug,
                        body = response.body.body,
                        image = response.body.image,
                        date_updated = DateUtils.convertServerStringDateToLong(response.body.image),
                        username = response.body.username
                    )
                    updateLocalDb(updatedBlogPost)
                    withContext(Main) {
                        onCompleteJob(
                            DataState.data(
                                data = BlogViewState(
                                    viewBlogFields = BlogViewState.ViewBlogFields(
                                        updatedBlogPost
                                    )

                                ),
                                response = Response(
                                    message = response.body.response,
                                    responseType = ResponseType.Toast()
                                )
                            )
                        )
                    }
                } else {
                    withContext(Main) {
                        onCompleteJob(
                            DataState.data(
                                data = BlogViewState(
                                    viewBlogFields = BlogViewState.ViewBlogFields(
                                        blogPost = BlogPost(-1, "", "", "", "", 0L, "")
                                    )
                                ),
                                response = Response(
                                    message = response.body.response,
                                    responseType = ResponseType.Toast()
                                )
                            )
                        )
                    }
                }

            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.updateBlogPost(
                    "Token ${authToken.token}",
                    slug,
                    title,
                    body,
                    image
                )
            }

            //not applicable
            override fun loadFromCache(): LiveData<BlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogPostDao.updateBlogPost(
                        it.pk,
                        it.title,
                        it.body,
                        it.image
                    )
                }
            }

            override fun setJob(job: Job) {
                addJob("updateBlogPost", job)
            }
        }.getAsLiveData()
    }


}