package com.example.mypowerfulandroidapp.repository.auth

import android.service.autofill.Dataset
import androidx.lifecycle.LiveData
import androidx.lifecycle.switchMap
import com.example.mypowerfulandroidapp.api.auth.OpenApiAuthService
import com.example.mypowerfulandroidapp.api.auth.network_response.LoginResponse
import com.example.mypowerfulandroidapp.api.auth.network_response.RegistrationResponse
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.Response
import com.example.mypowerfulandroidapp.ui.ResponseType
import com.example.mypowerfulandroidapp.ui.auth.state.AuthViewState
import com.example.mypowerfulandroidapp.util.ApiEmptyResponse
import com.example.mypowerfulandroidapp.util.ApiErrorResponse
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.ErrorHandling.Companion.ERROR_UNKNOWN
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import kotlinx.android.synthetic.main.activity_main.view.*

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
) {
    fun attemptLogin(email: String, password: String): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.login(email, password)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }
                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        response.errorMessage,
                                        ResponseType.Dialog()
                                    )
                                )
                            }
                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        ERROR_UNKNOWN,
                                        ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }

    fun attemptRegister(
        email: String,
        username: String,
        password: String,
        password_confirm: String
    ): LiveData<DataState<AuthViewState>> {
        return openApiAuthService.register(email, username, password, password_confirm)
            .switchMap { response ->
                object : LiveData<DataState<AuthViewState>>() {
                    override fun onActive() {
                        super.onActive()
                        when (response) {
                            is ApiSuccessResponse -> {
                                value = DataState.data(
                                    data = AuthViewState(
                                        authToken = AuthToken(
                                            response.body.pk,
                                            response.body.token
                                        )
                                    ),
                                    response = null
                                )
                            }
                            is ApiErrorResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        response.errorMessage,
                                        ResponseType.Dialog()
                                    )
                                )
                            }
                            is ApiEmptyResponse -> {
                                value = DataState.error(
                                    response = Response(
                                        ERROR_UNKNOWN,
                                        ResponseType.Dialog()
                                    )
                                )
                            }
                        }
                    }
                }
            }
    }
}