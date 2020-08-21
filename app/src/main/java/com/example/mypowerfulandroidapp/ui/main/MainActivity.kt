package com.example.mypowerfulandroidapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.BaseActivity
import com.example.mypowerfulandroidapp.ui.auth.AuthActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {
    private val TAG = "MainActivity"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tool_bar.setOnClickListener {
            sessionManager.logout()
        }
        subscribeToObservers();
    }

    private fun subscribeToObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeToObservers: $authToken")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navToAuthActivity()
            }
        })
    }

    private fun navToAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun displayProgressBar(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }
}