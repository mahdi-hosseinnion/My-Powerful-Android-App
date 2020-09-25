package com.example.mypowerfulandroidapp.repository

import android.app.Dialog
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.Response
import com.example.mypowerfulandroidapp.ui.ResponseType
import com.example.mypowerfulandroidapp.util.*
import com.example.mypowerfulandroidapp.util.Constants.Companion.NETWORK_TIMEOUT
import com.example.mypowerfulandroidapp.util.Constants.Companion.TESTING_CACHE_DELAY
import com.example.mypowerfulandroidapp.util.Constants.Companion.TESTING_NETWORK_DELAY
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_CHECK_NETWORK_CONNECTION
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.UNABLE_TODO_OPERATION_WO_INTERNET
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.UNABLE_TO_RESOLVE_HOST
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlin.concurrent.fixedRateTimer


abstract class NetworkBoundResource<ResponseObject, CacheObject, ViewStateType>(
    isConnectedToTheInternet: Boolean,
    isNetworkRequest: Boolean,
    shouldCancelIfNOInternet: Boolean,//if no internet should show dialog and say it?
    shouldLoadFromCache: Boolean

) {
    private val TAG = "NetworkBoundResource"
    protected val result = MediatorLiveData<DataState<ViewStateType>>()
    protected lateinit var job: CompletableJob
    protected lateinit var coroutineScope: CoroutineScope


    init {
        setJob(initNewJob())
        setValue(DataState.loading(true, null))
        if (shouldLoadFromCache) {
            val dbResource = loadFromCache()
            result.addSource(dbResource) {
                result.removeSource(dbResource)
                setValue(DataState.loading(true, cashedData = it))
            }
        }
        if (isNetworkRequest) {
            if (isConnectedToTheInternet) {
                doNetworkRequest()
            } else {
                if (shouldCancelIfNOInternet) {
                    Log.e(TAG, "NetworkBoundResource: NO Internet access.")
                    onErrorReturn(UNABLE_TODO_OPERATION_WO_INTERNET, true, false)
                } else {
                    doCacheRequest()
                }
            }
        } else {
            doCacheRequest()
        }
    }

    private fun doNetworkRequest() {
        coroutineScope.launch {
            //simulate network delay for testing
            delay(TESTING_NETWORK_DELAY)
            withContext(Main) {
                //make network call
                val apiResponse = createCall()
                Log.d(TAG, "Response: ${apiResponse.value}")
                result.addSource(apiResponse) { response ->
                    result.removeSource(apiResponse)
                    coroutineScope.launch {
                        handleApiResponse(response)
                    }
                }
            }
        }
        //for network timeout
        GlobalScope.launch(IO) {
            delay(NETWORK_TIMEOUT )
            if (!job.isCompleted) {
                Log.d(TAG, "NetworkBoundResource: NetWork TimeOUT...")
                job.cancel(CancellationException(UNABLE_TO_RESOLVE_HOST))
            }
        }
    }

    private fun doCacheRequest() {
        coroutineScope.launch {
            //fake delay for test
            delay(TESTING_CACHE_DELAY)
            //view Data From ONLY cache and return
            createCacheRequestAndReturn()
        }
    }

    private suspend fun handleApiResponse(response: GenericApiResponse<ResponseObject>) {
        when (response) {
            is ApiSuccessResponse -> {
                Log.d(TAG, "handleApiResponse: 00mm ApiSuccessResponse")
                handleApiSuccessResponse(response)
            }
            is ApiErrorResponse -> {
                Log.d(TAG, "handleApiResponse: 00mm ApiErrorResponse")
                Log.e(TAG, "handleApiResponse: ERROR: ${response.errorMessage}")
                onErrorReturn(response.errorMessage, true, false)
            }
            is ApiEmptyResponse -> {
                Log.e(TAG, "handleApiResponse: ERROR: HTTP code 204 Return NOTHING")
                onErrorReturn("HTTP Code = 204. Return NOTHING", true, false)
            }
        }


    }

    @UseExperimental(InternalCoroutinesApi::class)
    private fun initNewJob(): Job {
        Log.d(TAG, "initNewJob: called...")
        job = Job()
        job.invokeOnCompletion(
            onCancelling = true,
            invokeImmediately = true,
            handler = object : CompletionHandler {
                override fun invoke(cause: Throwable?) {
                    if (!job.isCompleted) {
                        Log.d(TAG, "invoke: job has been canceled...")
                        cause?.let {
                            onErrorReturn(it.message, false, true)
                        } ?: onErrorReturn(ERROR_UNKNOWN, false, true)
                    } else if (job.isCompleted) {
                        Log.d(TAG, "invoke: job completed...")
                        //DO NOTHING .should be handled already
                    }
                }

            })
        coroutineScope = CoroutineScope(IO + job)
        return job
    }

    fun onErrorReturn(errorMessage: String?, shouldUseDialog: Boolean, shouldUseToast: Boolean) {
        Log.e(TAG, "onErrorReturn:8 $errorMessage")
        var msg = errorMessage
        var useDialog = shouldUseDialog
        var responseType: ResponseType = ResponseType.None()

        if (msg == null) {
            msg = ERROR_UNKNOWN
        } else if (ErrorHandling.isNetworkError(msg)) {
            msg = ERROR_CHECK_NETWORK_CONNECTION
            useDialog = false

        }
        if (shouldUseToast) {
            responseType = ResponseType.Toast()
        }
        if (useDialog) {
            responseType = ResponseType.Dialog()
        }
        onCompleteJob(
            DataState.error(
                response = Response(
                    message = msg,
                    responseType = responseType
                )
            )
        )


    }

    fun onCompleteJob(dataState: DataState<ViewStateType>) {
        Log.d(TAG, "onCompleteJob: called")
        GlobalScope.launch(Main) {
            job.complete()
            setValue(dataState)
        }
    }

    private fun setValue(dataState: DataState<ViewStateType>) {
        result.value = dataState
    }

    fun getAsLiveData() = result as LiveData<DataState<ViewStateType>>

    abstract suspend fun createCacheRequestAndReturn()
    abstract suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<ResponseObject>)
    abstract fun createCall(): LiveData<GenericApiResponse<ResponseObject>>
    abstract fun loadFromCache(): LiveData<ViewStateType>
    abstract suspend fun updateLocalDb(cacheObject: CacheObject?)
    abstract fun setJob(job: Job)
}