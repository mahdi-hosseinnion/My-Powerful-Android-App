package com.example.mypowerfulandroidapp.di

import com.example.mypowerfulandroidapp.di.auth.AuthComponent
import com.example.mypowerfulandroidapp.di.main.MainComponent
import dagger.Module

@Module(
    subcomponents = [
        AuthComponent::class,
        MainComponent::class
    ]
)
class SubComponentsModule