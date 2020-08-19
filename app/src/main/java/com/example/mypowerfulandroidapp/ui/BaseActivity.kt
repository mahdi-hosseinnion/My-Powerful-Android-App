package com.example.mypowerfulandroidapp.ui

import com.example.mypowerfulandroidapp.di.DaggerAppComponent
import com.example.mypowerfulandroidapp.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(){

    @Inject
    lateinit var sessionManager: SessionManager


}