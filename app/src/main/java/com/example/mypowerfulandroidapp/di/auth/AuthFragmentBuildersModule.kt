package com.example.mypowerfulandroidapp.di.auth

import com.example.mypowerfulandroidapp.ui.auth.ForgotPasswordFragment
import com.example.mypowerfulandroidapp.ui.auth.LauncherFragment
import com.example.mypowerfulandroidapp.ui.auth.LoginFragment
import com.example.mypowerfulandroidapp.ui.auth.RegisterFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class AuthFragmentBuildersModule  {

    @ContributesAndroidInjector()
    abstract fun contributeLauncherFragment(): LauncherFragment

    @ContributesAndroidInjector()
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector()
    abstract fun contributeRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector()
    abstract fun contributeForgotPasswordFragment(): ForgotPasswordFragment

}
