package com.example.mypowerfulandroidapp.session

import android.app.Application
import com.example.mypowerfulandroidapp.persistence.AuthTokenDao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
){
}