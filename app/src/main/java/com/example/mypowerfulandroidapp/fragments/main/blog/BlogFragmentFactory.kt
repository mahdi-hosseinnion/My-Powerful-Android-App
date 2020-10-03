package com.example.mypowerfulandroidapp.fragments.main.blog

import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.di.main.MainScope
import com.example.mypowerfulandroidapp.ui.auth.ForgotPasswordFragment
import com.example.mypowerfulandroidapp.ui.auth.LauncherFragment
import com.example.mypowerfulandroidapp.ui.auth.LoginFragment
import com.example.mypowerfulandroidapp.ui.auth.RegisterFragment
import com.example.mypowerfulandroidapp.ui.main.blog.BlogFragment
import com.example.mypowerfulandroidapp.ui.main.blog.UpdateBlogFragment
import com.example.mypowerfulandroidapp.ui.main.blog.ViewBlogFragment
import javax.inject.Inject

@MainScope

class BlogFragmentFactory
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory,
    private val requestManager: RequestManager
) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String) =

        when (className) {

            BlogFragment::class.java.name -> {
                BlogFragment(viewModelFactory,requestManager)
            }

            UpdateBlogFragment::class.java.name -> {
                UpdateBlogFragment(viewModelFactory,requestManager)
            }

            ViewBlogFragment::class.java.name -> {
                ViewBlogFragment(viewModelFactory,requestManager)
            }

            else -> {
                BlogFragment(viewModelFactory,requestManager)
            }
        }


}