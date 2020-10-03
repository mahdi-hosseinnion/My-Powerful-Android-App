package com.example.mypowerfulandroidapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.di.auth.AuthScope
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.ui.auth.state.AuthStateEvent
import com.example.mypowerfulandroidapp.ui.auth.state.AuthViewState
import com.example.mypowerfulandroidapp.ui.auth.state.LoginFields
import com.example.mypowerfulandroidapp.util.ApiEmptyResponse
import com.example.mypowerfulandroidapp.util.ApiErrorResponse
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

@AuthScope
class LoginFragment
@Inject
constructor(
    private val viewModelFactory: ViewModelProvider.Factory
) : Fragment(R.layout.fragment_login) {

    val viewModel: AuthViewModel by viewModels {
        viewModelFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cancelActiveJobs()
    }

    private fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }

    private val TAG = "LoginFragment"


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: " + viewModel.hashCode())

        login_button.setOnClickListener {
            login()
        }
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.viewState.observe(viewLifecycleOwner, Observer { authViewState ->

            authViewState.login_fields?.let { loginFields ->
                loginFields.login_email?.let { input_email.setText(it) }
                loginFields.login_password?.let { input_password.setText(it) }
            }


        })
    }

    private fun login() {
        viewModel.setStatEvent(
            AuthStateEvent.LoginAttemptEvent(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.setLoginFields(
            LoginFields(
                input_email.text.toString(),
                input_password.text.toString()
            )
        )
    }
}