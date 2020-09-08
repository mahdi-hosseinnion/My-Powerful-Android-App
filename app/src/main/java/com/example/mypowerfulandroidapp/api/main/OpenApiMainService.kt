package com.example.mypowerfulandroidapp.api.main

import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.api.GenericResponse
import com.example.mypowerfulandroidapp.api.main.responses.BlogListSearchResponse
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

    @PUT("account/change_password/")
    @FormUrlEncoded
    fun changePassword(
        @Header("Authorization") authorization: String,
        @Field("old_password") oldPassword: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_new_password") confirm_new_password: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @GET("blog/list")
    fun searchListBlogPosts(
        @Header("Authorization") authorization: String,
        @Query("search") query: String
    ): LiveData<GenericApiResponse<BlogListSearchResponse>>

}