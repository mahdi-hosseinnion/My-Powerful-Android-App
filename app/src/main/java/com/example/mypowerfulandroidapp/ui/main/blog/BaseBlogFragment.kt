package com.example.mypowerfulandroidapp.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.di.Injectable
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import com.example.mypowerfulandroidapp.ui.UiCommunicationListener
import com.example.mypowerfulandroidapp.ui.main.blog.viewmodels.BlogViewModel
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.ClassCastException
import javax.inject.Inject

abstract class BaseBlogFragment :  Fragment(), Injectable {
    private val TAG = "BaseBlogFragment"
    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var uiCommunicationListener: UiCommunicationListener

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var viewModel: BlogViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(BlogViewModel::class.java)
        } ?: throw Exception("Invalid activity")

        cancelActiveJobs()

    }

    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    private fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: $context should implement DataStateChangeListener", e)
        }
        try {
            uiCommunicationListener = context as UiCommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: $context should implement UiCommunicationListener", e)
        }
    }
}