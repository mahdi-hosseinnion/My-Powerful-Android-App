package com.example.mypowerfulandroidapp

import android.app.Activity
import android.app.Application
import com.example.mypowerfulandroidapp.di.AppInjector
import com.example.mypowerfulandroidapp.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.DaggerApplication
import javax.inject.Inject

class BaseApplication: Application(),HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun activityInjector(): AndroidInjector<Activity> =dispatchingAndroidInjector

    override fun onCreate() {
        super.onCreate()
        AppInjector.init(this)

    }


}