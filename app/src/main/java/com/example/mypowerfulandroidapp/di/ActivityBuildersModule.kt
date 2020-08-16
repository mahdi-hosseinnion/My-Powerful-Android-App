package com.example.mypowerfulandroidapp.di

import dagger.Module
import com.example.mypowerfulandroidapp.di.auth.AuthFragmentBuildersModule
import com.example.mypowerfulandroidapp.di.auth.AuthModule
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.di.auth.AuthViewModelModule
import com.example.mypowerfulandroidapp.ui.auth.AuthActivity
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

}