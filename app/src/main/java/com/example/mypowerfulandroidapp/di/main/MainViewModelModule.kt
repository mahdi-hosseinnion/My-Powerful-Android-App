package com.example.mypowerfulandroidapp.di.main

import androidx.lifecycle.ViewModel
import com.example.mypowerfulandroidapp.di.ViewModelKey
import com.example.mypowerfulandroidapp.ui.main.account.AccountViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class MainViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    abstract fun bindAuthViewModel(accountViewModel: AccountViewModel): ViewModel
}