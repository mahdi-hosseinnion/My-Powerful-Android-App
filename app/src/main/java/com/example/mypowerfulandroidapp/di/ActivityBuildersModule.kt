package com.example.mypowerfulandroidapp.di

import dagger.Module
import com.example.mypowerfulandroidapp.di.auth.AuthFragmentBuildersModule
import com.example.mypowerfulandroidapp.di.auth.AuthModule
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.di.auth.AuthViewModelModule
import com.example.mypowerfulandroidapp.di.main.FragmentBuildersModule
import com.example.mypowerfulandroidapp.di.main.MainModule
import com.example.mypowerfulandroidapp.di.main.MainViewModelModule
import com.example.mypowerfulandroidapp.ui.auth.AuthActivity
import com.example.mypowerfulandroidapp.ui.main.MainActivity
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuildersModule {

    @AuthScope
    @ContributesAndroidInjector(
        modules = [AuthModule::class, AuthFragmentBuildersModule::class, AuthViewModelModule::class]
    )
    abstract fun contributeAuthActivity(): AuthActivity

    @ContributesAndroidInjector(
        modules = [MainModule::class, FragmentBuildersModule::class, MainViewModelModule::class]
    )
    abstract fun contributeMainActivity(): MainActivity

}