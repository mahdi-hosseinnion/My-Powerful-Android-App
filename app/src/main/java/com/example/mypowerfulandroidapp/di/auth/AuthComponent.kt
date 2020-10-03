package com.example.mypowerfulandroidapp.di.auth

import com.example.mypowerfulandroidapp.ui.auth.AuthActivity
import dagger.Subcomponent
@AuthScope
@Subcomponent(
    modules = [
        AuthModule::class,
        AuthViewModelModule::class,
        AuthFragmentModule::class
    ]
)
interface AuthComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }

    fun inject(authActivity: AuthActivity)


}
