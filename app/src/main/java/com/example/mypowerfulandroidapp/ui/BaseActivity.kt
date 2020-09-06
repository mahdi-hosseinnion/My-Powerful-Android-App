package com.example.mypowerfulandroidapp.ui

import android.content.Context
import android.util.Log
import android.view.inputmethod.InputMethodManager
import com.example.mypowerfulandroidapp.di.DaggerAppComponent
import com.example.mypowerfulandroidapp.session.SessionManager
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(),
    DataStateChangeListener {
    private val TAG = "BaseActivity"
    override fun onDataStateChange(dataState: DataState<*>?) {
        dataState?.let { dataState ->
            GlobalScope.launch(Main) {
                displayProgressBar(dataState.loading.isLoading)
                dataState.error?.let { errorState ->
                    handleStateError(errorState)
                }
                dataState.data?.let { data ->
                    data.response?.let { responseState ->
                        handleStateResponse(responseState)
                    }
                }
            }
        }
    }

    private fun handleStateError(state: Event<StateError>) {
        state.getContentIfNotHandled()?.let { it ->
            when (it.response.responseType) {
                is ResponseType.Toast -> {
                    it.response.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.response.message?.let { message ->
                        displayErrorDialog(message)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.response.message}")
                }
            }
        }
    }

    private fun handleStateResponse(state: Event<Response>) {
        state.getContentIfNotHandled()?.let { it ->
            when (it.responseType) {
                is ResponseType.Toast -> {
                    it.message?.let { message ->
                        displayToast(message)
                    }
                }
                is ResponseType.Dialog -> {
                    it.message?.let { message ->
                        displaySuccessDialog(message)
                    }
                }
                is ResponseType.None -> {
                    Log.e(TAG, "handleStateError: ${it.message}")
                }
            }
        }
    }


    abstract fun displayProgressBar(loading: Boolean)

    @Inject
    lateinit var sessionManager: SessionManager

    override fun hideSoftKeyboard() {
        if(currentFocus!=null){
            val inputMethodManager = getSystemService(

             Context.INPUT_METHOD_SERVICE)as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken,0)
        }
    }
}