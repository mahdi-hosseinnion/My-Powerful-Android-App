package com.example.mypowerfulandroidapp.fragments.main.account

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.example.mypowerfulandroidapp.di.main.MainScope
import com.example.mypowerfulandroidapp.ui.main.account.AccountFragment
import com.example.mypowerfulandroidapp.ui.main.account.ChangePasswordFragment
import com.example.mypowerfulandroidapp.ui.main.account.UpdateAccountFragment
import javax.inject.Inject
@MainScope
class AccountFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            AccountFragment::class.java.name -> {
                AccountFragment(viewModelFactory)
            }

            ChangePasswordFragment::class.java.name -> {
                ChangePasswordFragment(viewModelFactory)
            }

            UpdateAccountFragment::class.java.name -> {
                UpdateAccountFragment(viewModelFactory)
            }
            else -> {
                AccountFragment(viewModelFactory)
            }

        }


}