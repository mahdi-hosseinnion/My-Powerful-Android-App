package com.example.mypowerfulandroidapp.di.main

import com.example.mypowerfulandroidapp.ui.main.account.AccountFragment
import com.example.mypowerfulandroidapp.ui.main.account.ChangePasswordFragment
import com.example.mypowerfulandroidapp.ui.main.account.UpdateAccountFragment
import com.example.mypowerfulandroidapp.ui.main.blog.BlogFragment
import com.example.mypowerfulandroidapp.ui.main.blog.UpdateBlogFragment
import com.example.mypowerfulandroidapp.ui.main.blog.ViewBlogFragment
import com.example.mypowerfulandroidapp.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}