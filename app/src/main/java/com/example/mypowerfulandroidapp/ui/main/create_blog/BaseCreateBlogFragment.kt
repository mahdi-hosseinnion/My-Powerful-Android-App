package com.example.mypowerfulandroidapp.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import com.example.mypowerfulandroidapp.ui.UiCommunicationListener
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import javax.inject.Inject

abstract class BaseCreateBlogFragment : DaggerFragment() {

    val TAG: String = "AppDebug"

    @Inject
    lateinit var requestManager: RequestManager

    @Inject
    lateinit var providerFactory: ViewModelProviderFactory

    lateinit var uiCommunicationListener: UiCommunicationListener
    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var viewModel: CreateBlogViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)

        viewModel = activity?.run {
            ViewModelProvider(this, providerFactory).get(CreateBlogViewModel::class.java)
        } ?: throw Exception("Invalid Activity")

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
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
        try {
            uiCommunicationListener = context as UiCommunicationListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement UiCommunicationListener")
        }
    }
}