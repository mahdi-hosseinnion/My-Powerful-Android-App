package com.example.mypowerfulandroidapp.repository.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mypowerfulandroidapp.api.main.OpenApiMainService
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.repository.NetworkBoundResource
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountViewState
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AccountRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val accountPropertiesDao: AccountPropertiesDao,
    val sessionManager: SessionManager
) {
    private val TAG: String = "AppDebug"

    private var repositoryJob: Job? = null

    fun getAccountProperties(authToken: AuthToken): LiveData<DataState<AccountViewState>> {
        return object :
            NetworkBoundResource<AccountProperties, AccountProperties, AccountViewState>(
                sessionManager.isConnectedToTheInternet(),
                true,
                true
            ) {
            override fun loadFromCache(): LiveData<AccountViewState> {
                return accountPropertiesDao.searchByPK(authToken.account_pk!!)
                    .switchMap {
                        object : LiveData<AccountViewState>() {
                            override fun onActive() {
                                super.onActive()
                                value = AccountViewState(it)
                            }
                        }
                    }
            }

            override suspend fun updateLocalDb(cacheObject: AccountProperties) {
                accountPropertiesDao.updateAccountProperties(
                    cacheObject.pk,
                    cacheObject.email,
                    cacheObject.username
                )
            }

            override suspend fun createCacheRequestAndReturn() {
                withContext(Main){
                    //finishing by viewing the db cache
                    result.addSource(loadFromCache()){viewState->
                        onCompleteJob(
                            DataState.data(
                                viewState,
                                null
                            )
                        )
                    }
                }
            }

            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<AccountProperties>) {
                updateLocalDb(apiSuccessResponse.body)
                createCacheRequestAndReturn()
            }

            override fun createCall(): LiveData<GenericApiResponse<AccountProperties>> {
                return openApiMainService.getAccountProperties("Token ${authToken.token}")
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.getAsLiveData()
    }

    fun cancelActiveJobs() {
        Log.d(TAG, "AuthRepository: Cancelling on-going jobs...")
        repositoryJob?.cancel()
    }
}