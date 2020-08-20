package com.example.mypowerfulandroidapp.repository.auth

import android.service.autofill.Dataset
import android.util.Log
import androidx.core.content.contentValuesOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mypowerfulandroidapp.api.auth.OpenApiAuthService
import com.example.mypowerfulandroidapp.api.auth.network_response.LoginResponse
import com.example.mypowerfulandroidapp.api.auth.network_response.RegistrationResponse
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
import com.example.mypowerfulandroidapp.util.ApiEmptyResponse
import com.example.mypowerfulandroidapp.util.ApiErrorResponse
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.GENERIC_AUTH_ERROR
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.coroutines.Job

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    private var repositoryJob: Job? = null
    private  val TAG = "AuthRepository"

    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        //check for client error
        val loginFieldsErrors = LoginFields(email, password).isValidForLogin()
        if (!loginFieldsErrors.equals(LoginFields.LoginError.none())) {
            return returnErrorResponse(loginFieldsErrors, ResponseType.Dialog())
        }
        return object : NetworkBoundResource<LoginResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<LoginResponse>) {
                //handle server side error like invalid credentials that count event with 200 code
                if (apiSuccessResponse.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(apiSuccessResponse.body.errorMessage, true, false)
                }
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

            override fun createCall(): LiveData<GenericApiResponse<LoginResponse>> {
                return openApiAuthService.login(email, password)
            }

            override fun setJob(job: Job) {
                repositoryJob?.cancel()
                repositoryJob = job
            }
        }.getAsLiveData()
    }

    private fun returnErrorResponse(
        message: String,
        responseType: ResponseType
    ): LiveData<DataState<AuthViewState>> {
        Log.e(TAG, "returnErrorResponse: $message" )
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
        return object : NetworkBoundResource<RegistrationResponse, AuthViewState>(
            sessionManager.isConnectedToTheInternet()
        ) {
            override suspend fun handleApiSuccessResponse(apiSuccessResponse: ApiSuccessResponse<RegistrationResponse>) {
                //handle server side error like invalid credentials that count event with 200 code
                if (apiSuccessResponse.body.response.equals(GENERIC_AUTH_ERROR)) {
                    return onErrorReturn(apiSuccessResponse.body.errorMessage, true, false)
                }
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
        }.getAsLiveData()
    }

    fun cancelActiveJobs() {
        repositoryJob?.cancel()
    }
}