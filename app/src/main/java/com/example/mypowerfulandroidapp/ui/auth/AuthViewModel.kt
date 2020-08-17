package com.example.mypowerfulandroidapp.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.mypowerfulandroidapp.api.auth.network_response.LoginResponse
import com.example.mypowerfulandroidapp.api.auth.network_response.RegistrationResponse
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.repository.auth.AuthRepository
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import javax.inject.Inject

class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
) : ViewModel() {
    fun testLoginResponse(): LiveData<GenericApiResponse<LoginResponse>> {
        return authRepository.testLoginRequest(
            "ssmmhh1382@gmail.com",
            "mahdi1909924"
        )
    }

    fun testRegisterResponse(): LiveData<GenericApiResponse<RegistrationResponse>> {
        return authRepository.testRegisterRequest(
            "test29523@gmail.com",
            "mahdi84231",
            "mahdimahdi",
            "mahdimahdi"
        )
    }
}