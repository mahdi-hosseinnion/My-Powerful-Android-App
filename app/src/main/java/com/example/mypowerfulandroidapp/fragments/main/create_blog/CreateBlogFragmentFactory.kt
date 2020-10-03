package com.example.mypowerfulandroidapp.fragments.main.create_blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.di.main.MainScope
import com.example.mypowerfulandroidapp.ui.auth.ForgotPasswordFragment
import com.example.mypowerfulandroidapp.ui.auth.LauncherFragment
import com.example.mypowerfulandroidapp.ui.auth.LoginFragment
import com.example.mypowerfulandroidapp.ui.auth.RegisterFragment
import com.example.mypowerfulandroidapp.ui.main.create_blog.CreateBlogFragment
import javax.inject.Inject

@MainScope
class CreateBlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            CreateBlogFragment::class.java.name -> {
                CreateBlogFragment(viewModelFactory,requestManager)
            }
            else -> {
                CreateBlogFragment(viewModelFactory,requestManager)
            }
        }


}