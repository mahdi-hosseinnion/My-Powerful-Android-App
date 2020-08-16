package com.example.mypowerfulandroidapp.di


import androidx.lifecycle.ViewModelProvider
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory
import dagger.Binds
import dagger.Module

@Module
abstract class ViewModelFactoryModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelProviderFactory): ViewModelProvider.Factory
}