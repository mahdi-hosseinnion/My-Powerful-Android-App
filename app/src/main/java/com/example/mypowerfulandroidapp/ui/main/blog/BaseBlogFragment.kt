package com.example.mypowerfulandroidapp.ui.main.blog

import android.content.Context
import android.util.Log
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment
import java.lang.ClassCastException

abstract class BaseBlogFragment : DaggerFragment() {
    private val TAG = "BaseBlogFragment"
    lateinit var stateChangeListener: DataStateChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: $context should implement DataStateChangeListener", e)
        }
    }
}