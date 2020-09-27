package com.example.mypowerfulandroidapp.ui.main

import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory

interface MainDependencyProvider {
    fun getVMProviderFactory(): ViewModelProviderFactory
    fun getGlideRequestManager(): RequestManager
}