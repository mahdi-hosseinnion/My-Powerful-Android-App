package com.example.mypowerfulandroidapp.ui.main.blog

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
import java.lang.ClassCastException
import javax.inject.Inject

abstract class BaseBlogFragment
constructor(
    @LayoutRes
    private val layoutId:Int
) : Fragment(layoutId) {
    private val TAG = "BaseBlogFragment"

    lateinit var stateChangeListener: DataStateChangeListener
    lateinit var uiCommunicationListener: UiCommunicationListener


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        //restore state after process death
//        savedInstanceState?.let { inState ->
//            (inState[BLOG_VIEW_STATE_BUNDLE_KEY] as BlogViewState?)?.let { viewState ->
//                viewModel.setViewState(viewState)
//            }
//        }
//        cancelActiveJobs()
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//
//        val viewState = viewModel.viewState.value
//        viewState?.blogFields?.blogList = ArrayList()
//        outState.putParcelable(
//            BLOG_VIEW_STATE_BUNDLE_KEY,
//            viewState
//        )
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
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