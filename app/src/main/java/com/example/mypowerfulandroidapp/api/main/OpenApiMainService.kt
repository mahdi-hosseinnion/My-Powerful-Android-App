package com.example.mypowerfulandroidapp.api.main

import android.graphics.drawable.AnimatedImageDrawable
import androidx.lifecycle.LiveData
import com.example.mypowerfulandroidapp.api.GenericResponse
import com.example.mypowerfulandroidapp.api.main.responses.BlogCreateUpdateResponse
import com.example.mypowerfulandroidapp.api.main.responses.BlogListSearchResponse
import com.example.mypowerfulandroidapp.util.GenericApiResponse
import com.example.mypowerfulandroidapp.models.AccountProperties
import okhttp3.MultipartBody
import okhttp3.RequestBody
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
        @Query("search") query: String,
        @Query("ordering") ordering: String,
        @Query("page") page: Int
    ): LiveData<GenericApiResponse<BlogListSearchResponse>>

    @GET("blog/{slug}/is_author")
    fun isAuthorOfBlogPost(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @DELETE("blog/{slug}/delete")
    fun deleteBlogPost(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String
    ): LiveData<GenericApiResponse<GenericResponse>>

    @Multipart
    @PUT("blog/{slug}/update")
    fun updateBlogPost(
        @Header("Authorization") authorization: String,
        @Path("slug") slug: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): LiveData<GenericApiResponse<BlogCreateUpdateResponse>>

    @Multipart
    @POST("blog/create")
    fun createBlogPost(
        @Header("Authorization") authorization: String,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part image: MultipartBody.Part?
    ): LiveData<GenericApiResponse<BlogCreateUpdateResponse>>
}