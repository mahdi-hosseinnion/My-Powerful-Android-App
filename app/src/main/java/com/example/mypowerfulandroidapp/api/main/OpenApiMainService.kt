package com.example.mypowerfulandroidapp.api.main

import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import retrofit2.http.GET
import com.example.mypowerfulandroidapp.models.AccountProperties
import retrofit2.http.Field
import retrofit2.http.Header

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") Authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>
}