package com.example.mypowerfulandroidapp.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.mypowerfulandroidapp.R
import com.example.mypowerfulandroidapp.util.ApiEmptyResponse
import com.example.mypowerfulandroidapp.util.ApiErrorResponse
import com.example.mypowerfulandroidapp.util.ApiSuccessResponse


class RegisterFragment : BaseAuthFragment() {
    private val TAG = "RegisterFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: "+viewModel.hashCode())
        viewModel.testRegisterResponse().observe(viewLifecycleOwner, Observer { response->
            response?.let {
                when(it){
                    is ApiSuccessResponse ->{
                        Log.d(TAG, "aa onViewCreated: REGISTER REQUEST: ${it.body}")
                    }
                    is ApiErrorResponse ->{
                        Log.d(TAG, "aa onViewCreated: REGISTER REQUEST: ${it.errorMessage}")
                    }
                    is ApiEmptyResponse ->{
                        Log.d(TAG, "aa onViewCreated: REGISTER REQUEST: Empty Response")
                    }

                }
            }

        })
    }
}