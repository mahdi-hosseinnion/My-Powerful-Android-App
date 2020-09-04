package com.example.mypowerfulandroidapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.observe
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.models.AccountProperties
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_update_account.*

class UpdateAccountFragment : BaseAccountFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(viewLifecycleOwner) { dataState ->
            dataState?.let {
                stateChangeListener.onDataStateChange(it)
                it.data?.let { data ->
                    Log.d(TAG, "subscribeToObservers: $data")
                }
            }
        }
        viewModel.viewState.observe(viewLifecycleOwner) { viewState ->
            viewState.accountProperties?.let {
                setAccountProperties(it)
            }
        }
    }

    private fun setAccountProperties(accountProperties: AccountProperties) {
        input_email?.let {
            input_email.setText(accountProperties.email)
        }
        input_username?.let {
            input_username.setText(accountProperties.username)
        }
    }

    private fun saveChanges() {
        viewModel.setStatEvent(
            AccountStateEvent.UpdateAccountPropertiesEvent(
                input_email?.text.toString(),
                input_username?.text.toString()
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.update_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveChanges()
                return true
            }
        }
        return super.onOptionsItemSelected(item)

    }
}