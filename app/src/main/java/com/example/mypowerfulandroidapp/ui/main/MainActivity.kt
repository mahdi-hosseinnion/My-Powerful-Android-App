package com.example.mypowerfulandroidapp.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.bumptech.glide.RequestManager
import com.example.mypowerfulandroidapp.BaseApplication
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.AUTH_TOKEN_BUNDLE_KEY
import com.example.mypowerfulandroidapp.models.AuthToken
import com.example.mypowerfulandroidapp.ui.BaseActivity
import com.example.mypowerfulandroidapp.ui.auth.AuthActivity
import com.example.mypowerfulandroidapp.ui.main.account.BaseAccountFragment
import com.example.mypowerfulandroidapp.ui.main.account.ChangePasswordFragment
import com.example.mypowerfulandroidapp.ui.main.account.UpdateAccountFragment
import com.example.mypowerfulandroidapp.ui.main.blog.BaseBlogFragment
import com.example.mypowerfulandroidapp.ui.main.blog.UpdateBlogFragment
import com.example.mypowerfulandroidapp.ui.main.blog.ViewBlogFragment
import com.example.mypowerfulandroidapp.ui.main.create_blog.BaseCreateBlogFragment
import com.example.mypowerfulandroidapp.util.BottomNavController
import com.example.mypowerfulandroidapp.util.NAVIGATION_BACK_STACK_KEY
import com.example.mypowerfulandroidapp.util.setUpNavigation
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import javax.inject.Named


class MainActivity : BaseActivity(),
    BottomNavController.OnNavigationGraphChanged,
    BottomNavController.OnNavigationReselectedListener {
    private val TAG = "MainActivity"

    @Inject
    @Named("AccountFragmentFactory")
    lateinit var accountFragmentFactory: FragmentFactory

    @Inject
    @Named("BlogFragmentFactory")
    lateinit var blogFragmentFactory: FragmentFactory

    @Inject
    @Named("CreateBlogFragmentFactory")
    lateinit var createBlogFragmentFactory: FragmentFactory


    private lateinit var bottomNavigationView: BottomNavigationView

    private val bottomNavController by lazy(LazyThreadSafetyMode.NONE) {
        BottomNavController(
            this,
            R.id.main_fragments_container,
            R.id.menu_nav_blog,
            this
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBar()
        setupBottomNavigationView(savedInstanceState)
        subscribeToObservers();
        restoreSession(savedInstanceState)
    }

    override fun inject() {
        (application as BaseApplication).mainComponent()
            .inject(this)
    }

    private fun setupBottomNavigationView(savedInstanceState: Bundle?) {
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)
        bottomNavigationView.setUpNavigation(bottomNavController, this)
        if (savedInstanceState == null) {
            bottomNavController.setupBottomNavigationBackStack(null)
            bottomNavController.onNavigationItemSelected()
        }
        (savedInstanceState?.get(NAVIGATION_BACK_STACK_KEY) as IntArray?)?.let { intArray ->
            val backStack = BottomNavController.BackStack()
            backStack.addAll(intArray.toTypedArray())
            bottomNavController.setupBottomNavigationBackStack(backStack)
        }
    }

    private fun subscribeToObservers() {
        sessionManager.cachedToken.observe(this, Observer { authToken ->
            Log.d(TAG, "subscribeToObservers: $authToken")
            if (authToken == null || authToken.account_pk == -1 || authToken.token == null) {
                navToAuthActivity()
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(
            AUTH_TOKEN_BUNDLE_KEY,
            sessionManager.cachedToken.value
        )
        outState.putIntArray(
            NAVIGATION_BACK_STACK_KEY,
            bottomNavController.navigationBackStack.toIntArray()
        )
        super.onSaveInstanceState(outState)
    }

    private fun restoreSession(savedInstanceState: Bundle?) {
        savedInstanceState?.let { inState ->
            (inState[AUTH_TOKEN_BUNDLE_KEY] as AuthToken?)?.let { authToken ->
                sessionManager.setValue(authToken)
            }
        }
    }

    private fun navToAuthActivity() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
        (application as BaseApplication).releaseMainComponent()
    }

    override fun displayProgressBar(loading: Boolean) {
        if (loading) {
            progress_bar.visibility = View.VISIBLE
        } else {
            progress_bar.visibility = View.GONE
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(tool_bar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun expandAppBar() {
        findViewById<AppBarLayout>(R.id.app_bar).setExpanded(true)
    }

    override fun onBackPressed() = bottomNavController.onBackPressed()
//    override fun getNavGraphId(itemId: Int) = when (itemId) {
//        R.id.nav_blog -> {
//            R.navigation.nav_blog
//        }
//        R.id.nav_account -> {
//            R.navigation.nav_account
//        }
//        R.id.nav_create_blog -> {
//            R.navigation.nav_create_blog
//        }
//        else -> {
//            R.navigation.nav_blog
//        }
//    }

    override fun onGraphChange() {
        expandAppBar()
        cancelActiveJobs()

    }

    private fun cancelActiveJobs() {
        val fragments = bottomNavController.fragmentManager
            .findFragmentById(bottomNavController.containerId)
            ?.childFragmentManager
            ?.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                when (fragment) {
                    is BaseAccountFragment -> fragment.cancelActiveJobs()
                    is BaseBlogFragment -> fragment.cancelActiveJobs()
                    is BaseCreateBlogFragment -> fragment.cancelActiveJobs()
                }
            }
        }
        displayProgressBar(false)
    }

    override fun onReselectNavItem(navController: NavController, fragment: Fragment) =
        when (fragment) {
            is ViewBlogFragment -> {
                navController.navigate(R.id.action_viewBlogFragment_to_home)
            }
            is UpdateBlogFragment -> {
                navController.navigate(R.id.action_updateBlogFragment_to_home)
            }
            is ChangePasswordFragment -> {
                navController.navigate(R.id.action_changePasswordFragment_to_home)
            }
            is UpdateAccountFragment -> {
                navController.navigate(R.id.action_updateAccountFragment_to_home)
            }
            else -> {
                //do nothing
            }
        }


}