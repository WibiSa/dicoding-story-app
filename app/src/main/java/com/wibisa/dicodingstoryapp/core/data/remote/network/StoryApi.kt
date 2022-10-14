package com.wibisa.dicodingstoryapp.core.data.remote.network

import com.wibisa.dicodingstoryapp.core.data.remote.response.AllStoriesNetwork
import com.wibisa.dicodingstoryapp.core.data.remote.response.LoginResponse
import com.wibisa.dicodingstoryapp.core.data.remote.response.RegisterResponse
import retrofit2.http.*

interface StoryApi {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getAllStories(
        @Header("Authorization") token: String
    ): AllStoriesNetwork
}