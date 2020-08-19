package com.example.mypowerfulandroidapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mypowerfulandroidapp.api.auth.network_response.LoginResponse
import com.example.mypowerfulandroidapp.api.auth.network_response.RegistrationResponse
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.repository.auth.AuthRepository
import com.example.mypowerfulandroidapp.ui.BaseViewModel
import com.example.mypowerfulandroidapp.ui.DataState
import com.example.mypowerfulandroidapp.ui.auth.state.AuthStateEvent
import com.example.mypowerfulandroidapp.ui.auth.state.AuthViewState
import com.example.mypowerfulandroidapp.ui.auth.state.LoginFields
import com.example.mypowerfulandroidapp.ui.auth.state.RegistrationFields
import com.example.mypowerfulandroidapp.util.AbsentLiveData
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : BaseViewModel<AuthStateEvent, AuthViewState>() {


    override fun handleStateEvent(stateEvent: AuthStateEvent): LiveData<DataState<AuthViewState>> {
        when (stateEvent) {
            is AuthStateEvent.loginAttemptEvent -> {
                return AbsentLiveData.create()
            }
            is AuthStateEvent.registrationAttempEvent -> {
                return AbsentLiveData.create()
            }
            is AuthStateEvent.checkPreviousAuthEvent -> {
                return AbsentLiveData.create()
            }
        }
    }

    fun setRegistrationFields(registrationFields: RegistrationFields) {
        val update = getCurrentViewStateOrNew()
        if (update.registrationFields == registrationFields) {
            return
        }
        update.registrationFields = registrationFields
        _ViewState.value=update
    }
    fun setLoginFields(loginFields: LoginFields) {
        val update = getCurrentViewStateOrNew()
        if (update.login_fields == loginFields) {
            return
        }
        update.login_fields = loginFields
        _ViewState.value=update
    }
    fun setAuthToken(authToken: AuthToken) {
        val update = getCurrentViewStateOrNew()
        if (update.authToken == authToken) {
            return
        }
        update.authToken = authToken
        _ViewState.value=update
    }

    override fun initNewViewState(): AuthViewState {
        return AuthViewState()
    }
}