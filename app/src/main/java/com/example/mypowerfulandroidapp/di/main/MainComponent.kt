package com.example.mypowerfulandroidapp.di.main

import com.example.mypowerfulandroidapp.ui.main.MainActivity
import dagger.Subcomponent
@MainScope
@Subcomponent(
    modules = [
        MainModule::class,
        MainViewModelModule::class,
        MainFragmentsModule::class
    ]
)
interface MainComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): MainComponent
    }

    fun inject(mainActivity: MainActivity)


}
