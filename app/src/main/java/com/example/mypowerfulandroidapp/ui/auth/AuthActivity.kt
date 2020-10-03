package com.example.mypowerfulandroidapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.mypowerfulandroidapp.BaseApplication
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.fragments.auth.AuthNavHostFragment
import com.example.mypowerfulandroidapp.ui.BaseActivity
import com.example.mypowerfulandroidapp.ui.auth.state.AuthStateEvent
import com.example.mypowerfulandroidapp.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_auth.*
import javax.inject.Inject

class AuthActivity : BaseActivity() {
    private val TAG = "AuthActivity"

    @Inject
    lateinit var fragmentFactory: FragmentFactory

    @Inject
    lateinit var providerFactory: ViewModelProvider.Factory
    val viewModel: AuthViewModel by viewModels {
        providerFactory
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        onRestoreInstanceState()
        subscribeToObservers()
    }

    override fun inject() {
        (application as BaseApplication).authComponent().inject(this)
    }

    private fun onRestoreInstanceState() {
        val host = supportFragmentManager.findFragmentById(
            R.id.auth_fragment_container
        )
        host?.let {
            //do nothing
        } ?: createNavHost()
    }

    private fun createNavHost() {
        val navHost = AuthNavHostFragment.create(
            R.navigation.auth_nav_graph
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.auth_fragment_container,
                navHost,
                getString(R.string.AuthNavHost)
            ).setPrimaryNavigationFragment(navHost)
            .commit()
    }

    private fun subscribeToObservers() {

        viewModel.dataState.observe(this, Observer { dataState ->
            onDataStateChange(dataState)
            dataState.data?.let { data ->

                data.data?.let { event ->

                    event.getContentIfNotHandled()?.let { authViewState ->
                        authViewState.authToken?.let {
                            viewModel.setAuthToken(it)
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

    override fun onResume() {
        super.onResume()
        checkPreviousAuthUser()
    }

    override fun expandAppBar() {
        //do nothing
    }

    private fun checkPreviousAuthUser() {
        viewModel.setStatEvent(AuthStateEvent.CheckPreviousAuthEvent())
    }

    private fun navToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        (application as BaseApplication).releaseAuthComponent()
    }

    override fun displayProgressBar(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }


}