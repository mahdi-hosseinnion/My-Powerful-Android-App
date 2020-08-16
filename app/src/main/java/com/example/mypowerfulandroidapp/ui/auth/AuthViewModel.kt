package com.example.mypowerfulandroidapp.ui.auth

import androidx.lifecycle.ViewModel
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.repository.auth.AuthRepository
import javax.inject.Inject
class AuthViewModel
@Inject
constructor(
    val authRepository: AuthRepository
):ViewModel(){
}