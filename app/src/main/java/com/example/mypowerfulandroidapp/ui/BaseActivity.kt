package com.example.mypowerfulandroidapp.ui

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.inputmethod.InputMethodManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mypowerfulandroidapp.di.DaggerAppComponent
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.util.Constants.Companion.PERMISSION_REQUEST_READ_STORAGE
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.jar.Manifest
import javax.inject.Inject

abstract class BaseActivity : DaggerAppCompatActivity(),
    DataStateChangeListener,
    UiCommunicationListener {
    private val TAG = "BaseActivity"


    @Inject
    lateinit var sessionManager: SessionManager
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

    override fun onUiMessageReceived(uiMessage: UIMessage) {
        when (uiMessage.uiMessageType) {
            is UiMessageType.Toast -> {
                displayToast(uiMessage.message)
            }
            is UiMessageType.Dialog -> {
                displayInfoDialog(uiMessage.message)
            }
            is UiMessageType.AreYouSureDialog -> {
                areYouSureDialog(
                    uiMessage.message,
                    uiMessage.uiMessageType.callback
                )
            }
            is UiMessageType.None -> {
                Log.d(TAG, "onUiMessageReceived: None case message: ${uiMessage.message}")
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


    override fun hideSoftKeyboard() {
        if (currentFocus != null) {
            val inputMethodManager = getSystemService(

                Context.INPUT_METHOD_SERVICE
            ) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
    }

    override fun isStoragePermissionGranted(): Boolean {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
            &&
            ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                PERMISSION_REQUEST_READ_STORAGE
            )
            return false
        } else {
            return true
        }
    }
}