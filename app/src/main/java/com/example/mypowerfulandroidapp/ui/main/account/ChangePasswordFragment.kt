package com.example.mypowerfulandroidapp.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.ui.main.account.state.AccountStateEvent
import com.example.mypowerfulandroidapp.util.SuccessHandling.Companion.RESPONSE_PASSWORD_UPDATE_SUCCESS
import kotlinx.android.synthetic.main.fragment_change_password.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChangePasswordFragment : BaseAccountFragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        update_password_button.setOnClickListener {
            viewModel.setStatEvent(
                AccountStateEvent.ChangePasswordEvent(
                    input_current_password?.text.toString(),
                    input_new_password?.text.toString(),
                    input_confirm_new_password?.text.toString()
                )
            )
        }
        subscribeToObservers()

    }

    private fun subscribeToObservers() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer {
            stateChangeListener.onDataStateChange(it)
            it?.let { dataState ->
                dataState.data?.let { data ->
                    data.response?.let { event ->
                        if (event.peekContent().message.equals(RESPONSE_PASSWORD_UPDATE_SUCCESS)) {
                            stateChangeListener.hideSoftKeyboard()
                            findNavController().popBackStack()
                        }
                    }
                }
            }
        })

    }
}