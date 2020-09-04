package com.example.mypowerfulandroidapp.api.main

import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.api.GenericResponse
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import com.example.mypowerfulandroidapp.models.AccountProperties
import retrofit2.http.*

interface OpenApiMainService {

    @GET("account/properties")
    fun getAccountProperties(
        @Header("Authorization") Authorization: String
    ): LiveData<GenericApiResponse<AccountProperties>>

    @PUT("account/properties/update")
    @FormUrlEncoded
    fun updateAccountProperties(
        @Header("Authorization") authorization: String,
        @Field("email") email: String,
        @Field("username") username: String
    ): LiveData<GenericApiResponse<GenericResponse>>
}