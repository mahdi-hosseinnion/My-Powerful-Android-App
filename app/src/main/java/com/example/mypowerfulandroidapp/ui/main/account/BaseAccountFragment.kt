package com.example.mypowerfulandroidapp.ui.main.account


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.di.Injectable
import com.example.mypowerfulandroidapp.ui.DataStateChangeListener
import com.example.mypowerfulandroidapp.ui.main.MainDependencyProvider
import com.example.mypowerfulandroidapp.ui.main.account.state.ACCOUNT_VIEW_STATE_BUNDLE_KEY
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountViewState
import com.example.mypowerfulandroidapp.viewmodels.ViewModelProviderFactory
import dagger.android.support.DaggerFragment
import java.lang.Exception
import javax.inject.Inject


abstract class BaseAccountFragment :  Fragment(), Injectable {

    val TAG: String = "AppDebug"

    lateinit var stateChangeListener: DataStateChangeListener

    lateinit var mainDependencyProvider: MainDependencyProvider

    lateinit var viewModel: AccountViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupActionBarWithNavController(R.id.accountFragment, activity as AppCompatActivity)

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = activity?.run {
            ViewModelProvider(this, mainDependencyProvider.getVMProviderFactory()
            ).get(AccountViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        //restore state after process death
        savedInstanceState?.let {inState->
            (inState[ACCOUNT_VIEW_STATE_BUNDLE_KEY] as AccountViewState?)?.let {viewState->
                viewModel.setViewState(viewState)
            }
        }
        cancelActiveJobs()
    }
    private fun isViewModelInitialized()=::viewModel.isInitialized

    override fun onSaveInstanceState(outState: Bundle) {
        if (isViewModelInitialized()){
            outState.putParcelable(
                ACCOUNT_VIEW_STATE_BUNDLE_KEY,
                viewModel.viewState.value
            )
        }
        super.onSaveInstanceState(outState)
    }


    fun cancelActiveJobs() {
        viewModel.cancelActiveJobs()
    }
    fun setupActionBarWithNavController(fragmentId: Int, activity: AppCompatActivity) {
        val appBarConfiguration = AppBarConfiguration(setOf(fragmentId))
        NavigationUI.setupActionBarWithNavController(
            activity,
            findNavController(),
            appBarConfiguration
        )
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            stateChangeListener = context as DataStateChangeListener
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement DataStateChangeListener")
        }
        try {
            mainDependencyProvider = context as MainDependencyProvider
        } catch (e: ClassCastException) {
            Log.e(TAG, "$context must implement MainDependencyProvider")
        }
    }
}

