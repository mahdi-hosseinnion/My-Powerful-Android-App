package com.example.mypowerfulandroidapp.ui.main.blog

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment
import java.lang.ClassCastException

abstract class BaseBlogFragment : DaggerFragment() {
    private val TAG = "BaseBlogFragment"
    lateinit var stateChangeListener: DataStateChangeListener
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.blogFragment, activity as AppCompatActivity)
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
    }
}