package com.example.mypowerfulandroidapp.repository.auth

import com.example.mypowerfulandroidapp.api.auth.OpenApiAuthService
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import com.example.mypowerfulandroidapp.session.SessionManager

class AuthRepository
constructor(
    val authTokenDao: AuthTokenDao,
    val openApiAuthService: OpenApiAuthService,
    val sessionManager: SessionManager
){
}