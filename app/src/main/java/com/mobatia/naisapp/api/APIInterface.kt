package com.mobatia.naisapp.api

import com.mobatia.naisapp.activity.login.model.LoginResponseModel
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface APIInterface {

    @POST("Api-V1/login")
    @FormUrlEncoded
    fun getLogin(
        @Field("grant_type") grant_type: String?,
        @Field("client_id") client_id: String?,
        @Field("client_secret") client_secret: String?,
        @Field("username") username: String?,
        @Field("email") email: String?,
        @Field("password") password: String?,
        @Field("deviceid") deviceid: String?,
        @Field("devicetype") devicetype: String?,
        @Field("device_name") device_name: String?,
        @Field("app_version") app_version: String?,
        @Field("device_identifier") device_identifier: String?
    ): Call<LoginResponseModel>
}