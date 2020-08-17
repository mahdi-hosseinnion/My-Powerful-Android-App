package com.example.mypowerfulandroidapp.repository.auth

import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.api.auth.OpenApiAuthService
import com.example.mypowerfulandroidapp.api.auth.network_response.LoginResponse
import com.example.mypowerfulandroidapp.api.auth.network_response.RegistrationResponse
import com.example.mypowerfulandroidapp.persistence.AccountPropertiesDao
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.util.GenericApiResponse

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val accountPropertiesDao: AccountPropertiesDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
){
    fun testLoginRequest(email:String,password:String):LiveData<GenericApiResponse<LoginResponse>>{
        return openApiAuthService.login(email,password)
    }
    fun testRegisterRequest(email:String,username:String,password:String,password2:String):LiveData<GenericApiResponse<RegistrationResponse>>{
        return openApiAuthService.register(email,username,password,password2)
    }
}