package com.example.mypowerfulandroidapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.BaseActivity
import com.example.mypowerfulandroidapp.ui.ResponseType
import com.example.mypowerfulandroidapp.ui.auth.state.AuthViewState
import com.example.mypowerfulandroidapp.ui.main.MainActivity
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity() {
    private val TAG = "AuthActivity"

    @Inject
    lateinit var viewModelProvider: ViewModelProviderFactory
    private lateinit var viewModel: AuthViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel = ViewModelProvider(this, viewModelProvider).get(AuthViewModel::class.java)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(this, Observer { dataState ->
            dataState.data?.let { data ->

                data.data?.let { event ->

                    event.getContentIfNotHandled()?.let { authViewState ->
                        authViewState.authToken?.let {
                            viewModel.setAuthToken(it)
                        }
                    }
                }
                data.response?.let {
                    it.getContentIfNotHandled().let { response ->
                        response?.let { response ->
                            when (response.responseType) {
                                is ResponseType.Dialog -> {
                                    //inflate error dialog
                                }
                                is ResponseType.Toast -> {
                                    //show toast
                                }
                                is ResponseType.None -> {
                                    Log.e(TAG, "subscribeToObservers: Response: ${response.message}")
                                }

                            }
                        }
                    }
                }
            }
        })
        viewModel.viewState.observe(this, Observer { authViewState ->
            authViewState.authToken?.let {
                sessionManager.login(it)
            }
        })
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeToObservers: $authToken")
            if (authToken != null && authToken.account_pk != -1 && authToken.token != null) {
                navToMainActivity()
            }
        })

    }

    private fun navToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}