package com.example.mypowerfulandroidapp.ui.main.account

import android.content.Context
import android.util.Log
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import dagger.android.support.DaggerFragment


abstract class BaseAccountFragment : DaggerFragment(){

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            stateChangeListener = context as DataStateChangeListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement DataStateChangeListener" )
        }
    }
}

