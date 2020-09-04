package com.example.mypowerfulandroidapp.repository.auth

import android.content.SharedPreferences
import android.service.autofill.Dataset
import android.util.Log
import androidx.core.content.contentValuesOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mypowerfulandroidapp.api.auth.OpenApiAuthService
import com.example.mypowerfulandroidapp.api.auth.network_response.LoginResponse
import com.example.mypowerfulandroidapp.api.auth.network_response.RegistrationResponse
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import com.example.mypowerfulandroidapp.repository.NetworkBoundResource
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.*
import com.example.mypowerfulandroidapp.ui.auth.RegisterFragment
import com.example.mypowerfulandroidapp.ui.auth.state.AuthViewState
import com.example.mypowerfulandroidapp.ui.auth.state.LoginFields
import com.example.mypowerfulandroidapp.ui.auth.state.RegistrationFields
import com.example.mypowerfulandroidapp.util.*
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_SAVE_AUTH_TOKEN
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.Job

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager,
    val sharedPreferences: SharedPreferences,
    val sharedPrefsEditor: SharedPreferences.Editor
) {
    private var repositoryJob: Job? = null
    private val TAG = "AuthRepository"

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        //check for client error
        val loginFieldsErrors = LoginFields(email, password).isValidForLogin()
        if (!loginFieldsErrors.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldsErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse,Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = false

        ) {


            override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<LoginResponse>) {
                //handle server side error like invalid credentials that count event with 200 code
                if (response.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(response.body.errorMessage, true, false)
                }
                //don't care about result just insert if it doesn't exist b/c foreign key relationship. with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        response.body.pk,
                        response.body.email,
                        ""
                    )
                )
                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        response.body.pk,
                        response.body.token
                    )
                )
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            response = Response(
                                ERROR_SAVE_AUTH_TOKEN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }
                saveAuthenticatedUserToPrefs(email);

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                response.body.pk,
                                response.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            //not use in this case
            override suspend fun createCacheRequestAndReturn() {}
            //not use in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }
            //not use in this case
            override suspend fun updateLocalDb(cacheObject: Any) {}
        }.getAsLiveData()
    }

    private fun saveAuthenticatedUserToPrefs(email: String) {
        sharedPrefsEditor.putString(PreferenceKeys.PREVIOUS_AUTH_USER, email)
        sharedPrefsEditor.apply()
    }

    private fun returnErrorResponse(
        message: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        Log.e(TAG, "returnErrorResponse: $message")
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.error(
                    response = Response(
                        message,
                        responseType

                    )
                )

            }
        }
    }


    fun attemptRegister(
        email: String,
        username: String,
        password: String,
        password_confirm: String
    ): LiveData<DataState<AuthViewState>> {
        //check for client error
        val registrationFieldsErrors =
            RegistrationFields(email, username, password, password_confirm).isValidForRegistration()
        if (!registrationFieldsErrors.equals(RegistrationFields.RegistrationError.none())) {
            return returnErrorResponse(registrationFieldsErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<RegistrationResponse,Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = true,
            shouldLoadFromCache = false
        ) {
            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<RegistrationResponse>) {
                //handle server side error like invalid credentials that count event with 200 code
                if (apiSuccessResponse.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(apiSuccessResponse.body.errorMessage, true, false)
                }
                //don't care about result just insert if it doesn't exist b/c foreign key relationship. with AuthToken table
                accountPropertiesDao.insertOrIgnore(
                    AccountProperties(
                        apiSuccessResponse.body.pk,
                        apiSuccessResponse.body.email,
                        ""
                    )
                )
                //will return -1 if failure
                val result = authTokenDao.insert(
                    AuthToken(
                        apiSuccessResponse.body.pk,
                        apiSuccessResponse.body.token
                    )
                )
                if (result < 0) {
                    return onCompleteJob(
                        DataState.error(
                            response = Response(
                                ERROR_SAVE_AUTH_TOKEN,
                                ResponseType.Dialog()
                            )
                        )
                    )
                }
                saveAuthenticatedUserToPrefs(email);

                onCompleteJob(
                    DataState.data(
                        data = AuthViewState(
                            authToken = AuthToken(
                                apiSuccessResponse.body.pk,
                                apiSuccessResponse.body.token
                            )
                        )
                    )
                )
            }

            override fun createCall(): LiveData<GenericApiResponse<RegistrationResponse>> {
                return openApiAuthService.register(email, username, password, password_confirm)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }

            //not use in this case
            override suspend fun createCacheRequestAndReturn() {}
            //not use in this case
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }
            //not use in this case
            override suspend fun updateLocalDb(cacheObject: Any) {}
        }.getAsLiveData()
    }

    fun checkPreviousAuthUser(): LiveData<DataState<AuthViewState>> {
        val previousAuthUser: String? =
            sharedPreferences.getString(PreferenceKeys.PREVIOUS_AUTH_USER, null)
        if (previousAuthUser.isNullOrBlank()) {
            return returnNoTokenFound()
        }
        return object : NetworkBoundResource<Void,Any, AuthViewState>(
            sessionManager.isConnectedToTheInternet(),
            isNetworkRequest = false,
            shouldLoadFromCache = false
        ) {
            override suspend fun createCacheRequestAndReturn() {
                accountPropertiesDao.searchByEmail(previousAuthUser).let { accountProperties ->
                    accountProperties?.let {
                        if (accountProperties.pk > -1) {
                            authTokenDao.searchByPk(accountProperties.pk).let { authToken ->
                                if (authToken != null) {
                                    onCompleteJob(
                                        DataState.data(
                                            data = AuthViewState(
                                                authToken = authToken
                                            )
                                        )
                                    )
                                    return
                                }

                            }
                        }
                    }
                    Log.d(TAG, "createCacheRequestAndReturn: Auth Token not found...")
                    onCompleteJob(
                        DataState.data(
                            response = Response(
                                RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                                ResponseType.None()
                            )
                        )
                    )
                }
            }

            //not user in this case
            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<Void>) {
            }

            //not use in this case
            override fun createCall(): LiveData<GenericApiResponse<Void>> {
                return AbsentLiveData.create()
            }
            override fun loadFromCache(): LiveData<AuthViewState> {
                return AbsentLiveData.create()
            }
            //not use in this case
            override suspend fun updateLocalDb(cacheObject: Any) {}

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.getAsLiveData()
    }

    private fun returnNoTokenFound(): LiveData<DataState<AuthViewState>> {
        return object : LiveData<DataState<AuthViewState>>() {
            override fun onActive() {
                super.onActive()
                value = DataState.data(
                    response = Response(
                        RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE,
                        ResponseType.None()
                    )
                )
            }
        }
    }

    fun cancelActiveJobs() {
        repositoryJob?.cancel()
    }
}