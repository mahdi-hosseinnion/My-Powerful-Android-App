package com.example.mypowerfulandroidapp.session

import android.app.Application
import com.example.mypowerfulandroidapp.persisitance.AuthTokenDao

class SessionManager
constructor(
    val authTokenDao: AuthTokenDao,
    val application: Application
){
}