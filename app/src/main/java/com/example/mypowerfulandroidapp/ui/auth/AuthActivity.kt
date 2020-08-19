package com.example.mypowerfulandroidapp.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.BaseActivity
import com.example.mypowerfulandroidapp.ui.main.MainActivity
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory
import javax.inject.Inject

class AuthActivity : BaseActivity() {
    private  val TAG = "AuthActivity"
    @Inject
    lateinit var viewModelProvider: ViewModelProviderFactory
    private lateinit var viewModel: AuthViewModel




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        viewModel=ViewModelProvider(this,viewModelProvider).get(AuthViewModel::class.java)
        subscribeToObservers()
    }
    private fun subscribeToObservers(){
        viewModel.viewState.observe(this, Observer {authViewState->
            authViewState.authToken?.let {
                sessionManager.login(it)
            }
        })
        sessionManager.cachedToken.observe(this, Observer {authToken->
            Log.d(TAG, "subscribeToObservers: $authToken")
            if (authToken!=null&&authToken.account_pk!=-1&&authToken.token!=null){
                navToMainActivity()
            }
        })

    }
    private fun navToMainActivity(){
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}