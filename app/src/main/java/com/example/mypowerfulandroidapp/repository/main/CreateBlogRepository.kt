package com.example.mypowerfulandroidapp.repository.main

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.api.main.OpenApiMainService
import com.example.mypowerfulandroidapp.api.main.responses.BlogCreateUpdateResponse
import com.example.mypowerfulandroidapp.di.main.MainScope
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.models.BlogPost
import com.example.mypowerfulandroidapp.persistence.BlogPostDao
import com.example.mypowerfulandroidapp.repository.JobManager
import com.example.mypowerfulandroidapp.repository.NetworkBoundResource
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.Response
import com.example.mypowerfulandroidapp.ui.ResponseType
import com.example.mypowerfulandroidapp.ui.main.create_blog.CreateBlogViewModel
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogViewState
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.DateUtils
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import javax.inject.Inject
import kotlin.random.Random
@MainScope
class CreateBlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
) : JobManager("CreateBlogRepository") {

    fun createNewBlogPost(
        authToken: AuthToken,
        title: RequestBody,
        body: RequestBody,
        image: MultipartBody.Part?
    ): LiveData<DataState<CreateBlogViewState>> {
        return object :
            NetworkBoundResource<BlogCreateUpdateResponse, BlogPost, CreateBlogViewState>(
                sessionManager.isConnectedToTheInternet(),//TODO("BUG")
                true,
                true,
                false
            ) {
            //not applicable
            override suspend fun createCacheRequestAndReturn() {
            }

            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<BlogCreateUpdateResponse>) {
                //if you don't have codingWithMitch.com account it will still return a 200
                //Need an Account for that
                if (response.body.response != RESPONSE_MUST_BECOME_CODINGWITHMITCH_MEMBER) {
                    val blogPost = BlogPost(
                        pk = response.body.pk,
                        body = response.body.body,
                        title = response.body.title,
                        slug = response.body.slug,
                        image = response.body.image,
                        date_updated = DateUtils.convertServerStringDateToLong(response.body.date_updated),
                        username = response.body.username
                    )
                    updateLocalDb(blogPost)
                }
                withContext(Main) {
                    onCompleteJob(
                        DataState.data(
                            null,
                            Response(
                                response.body.response,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }
            }

            override fun createCall(): LiveData<GenericApiResponse<BlogCreateUpdateResponse>> {
                return openApiMainService.createBlogPost(
                    "Token ${authToken.token}",
                    title,
                    body,
                    image
                )
            }

            //not applicable
            override fun loadFromCache(): LiveData<CreateBlogViewState> {
                return AbsentLiveData.create()
            }

            override suspend fun updateLocalDb(cacheObject: BlogPost?) {
                cacheObject?.let {
                    blogPostDao.insertOrReplace(it)
                }
            }

            override fun setJob(job: Job) {
                addJob("createNewBlogPost", job)
            }
        }.getAsLiveData()
    }

    fun testingMethod(
        title: RequestBody,
        body: RequestBody,
        image1: MultipartBody.Part?
    ): BlogPost {

        return BlogPost(
            pk = Random.nextInt(1000,100000),
            body = body.toString(),
            title = title.toString(),
            slug = title.toString(),
            image =  "https://www.spox.com/de/sport/fussball/international/spanien/2008/Bilder/600/messi-1-1200_600x347.jpg",
            date_updated = Random.nextInt(1000,100000).div(13L),
            username = "mahdi"
        )



    }
}