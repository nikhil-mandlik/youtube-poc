package com.nikhil.here.youtube_poc.di

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthService {

    @GET("auth")
    suspend fun fetchToken(
        @Query("client_id") clientId : String,
        @Query("redirect_uri") redirectUri : String,
        @Query("response_type") responseType : String,
        @Query("scope") scope : String,
    ) : Call<String>

    @GET("tokeninfo")
    suspend fun getTokenInfo(
        @Query("id_token") token : String
    )




}