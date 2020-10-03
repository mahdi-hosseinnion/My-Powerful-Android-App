package com.example.mypowerfulandroidapp.ui.main.create_blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import com.example.mypowerfulandroidapp.ui.UiCommunicationListener
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CREATE_BLOG_VIEW_STATE_BUNDLE_KEY
import com.example.mypowerfulandroidapp.ui.main.create_blog.state.CreateBlogViewState

abstract class BaseCreateBlogFragment
    constructor(
        @LayoutRes
        private val layoutId:Int
    )
    : Fragment(layoutId) {

    val TAG: String = "AppDebug"


    lateinit var uiCommunicationListener: UiCommunicationListener
    lateinit var stateChangeListener: DataStateChangeListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.createBlogFragment, activity as AppCompatActivity)

    }
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //restore state after process death
//        savedInstanceState?.let {inState->
//            (inState[CREATE_BLOG_VIEW_STATE_BUNDLE_KEY] as CreateBlogViewState?)?.let { viewState->
//                viewModel.setViewState(viewState)
//            }
//        }
//        cancelActiveJobs()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//            outState.putParcelable(
//                CREATE_BLOG_VIEW_STATE_BUNDLE_KEY,
//                viewModel.viewState.value
//            )
//        super.onSaveInstanceState(outState)
//    }
    abstract fun cancelActiveJobs()

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