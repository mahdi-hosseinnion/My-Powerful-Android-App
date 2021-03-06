package com.example.mypowerfulandroidapp.ui.main.create_blog

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.repository.main.CreateBlogRepository
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.BaseViewModel
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.Loading
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogStateEvent
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogViewState
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import okhttp3.MediaType
import okhttp3.RequestBody
import javax.inject.Inject

class CreateBlogViewModel
@Inject
constructor(
    val createBlogRepository: CreateBlogRepository,
    val sessionManager: SessionManager
) : BaseViewModel<CreateBlogStateEvent, CreateBlogViewState>() {


    override fun handleStateEvent(stateEvent: CreateBlogStateEvent): LiveData<DataState<CreateBlogViewState>> {
        return when (stateEvent) {
            is CreateBlogStateEvent.CreateNewBlogPostEvent -> {
                sessionManager.cachedToken.value?.let {
                    val title = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.title
                    )
                    val body = RequestBody.create(
                        MediaType.parse("text/plain"),
                        stateEvent.body
                    )
                    createBlogRepository.createNewBlogPost(
                        it,
                        title,
                        body,
                        stateEvent.image
                    )

                } ?: AbsentLiveData.create()
            }
            is CreateBlogStateEvent.None -> {
                liveData {
                    emit(DataState(loading = Loading(false)))
                }
            }
        }
    }

    override fun initNewViewState(): CreateBlogViewState {
        return CreateBlogViewState()
    }

    fun setNewBlogFields(title: String?, body: String?, image: Uri?) {
        val update = getCurrentViewStateOrNew()
        val newBlogFields = update.blogFields
        title?.let { newBlogFields.title = it }
        body?.let { newBlogFields.body = it }
        image?.let { newBlogFields.image = it }
        update.blogFields = newBlogFields
        setViewState(update)
    }

    fun clearNewBlogFields() {
        val update = getCurrentViewStateOrNew()
        update.blogFields = CreateBlogViewState.NewBlogFields()
        setViewState(update)
    }

    fun getImageUri(): Uri? {
        getCurrentViewStateOrNew().let { viewState ->
            viewState?.blogFields.let {
                return it.image
            }
        }
    }

    fun cancelActiveJobs() {
        createBlogRepository.cancelActiveJobs()
        handlePendingData()

    }

    private fun handlePendingData() {
        setStatEvent(CreateBlogStateEvent.None())
    }

    override fun onCleared() {
        super.onCleared()
        cancelActiveJobs()
    }


}