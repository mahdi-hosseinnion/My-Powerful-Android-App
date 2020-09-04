package com.example.mypowerfulandroidapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.session.SessionManager
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

class AccountFragment : BaseAccountFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        setHasOptionsMenu(true)
        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }
        logout_button.setOnClickListener {
            viewModel.logout()
        }
    }

    fun subscribeObservers() {
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let {
                it.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let { viewState ->
                            viewState.accountProperties?.let { accountProperties ->
                                Log.d(
                                    TAG,
                                    "subscribeObservers: accountProperties: $accountProperties"
                                )
                                viewModel.setAccountProperties(accountProperties)
                            }
                        }
                    }
                }
            }
            viewModel.viewState.observe(viewLifecycleOwner) { accountViewState ->
                accountViewState?.let {
                    it.accountProperties?.let { accountProperties ->
                        setAccountProperties(accountProperties)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setStatEvent(AccountStateEvent.GetAccountPropertiesEvent())
    }

    fun setAccountProperties(accountProperties: AccountProperties) {
        email?.text = accountProperties.email
        username?.text = accountProperties.username
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
            }
        }
        return super.onOptionsItemSelected(item)
    }
}