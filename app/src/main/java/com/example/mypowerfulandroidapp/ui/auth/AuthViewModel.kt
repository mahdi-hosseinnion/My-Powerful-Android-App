package com.example.mypowerfulandroidapp.ui.auth

import androidx.lifecycle.ViewModel
import com.example.mypowerfulandroidapp.repository.auth.AuthRepository

class AuthViewModel
constructor(
    val authRepository: AuthRepository
):ViewModel(){
}