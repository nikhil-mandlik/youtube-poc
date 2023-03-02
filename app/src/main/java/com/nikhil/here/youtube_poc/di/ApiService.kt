package com.nikhil.here.youtube_poc.di

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @GET("activities")
    suspend fun fetchUserActivities(
        @Query("part") part : String,
        @Query("maxResults") maxResults : Int,
        @Query("mine") mine : Boolean,
        @HeaderMap headers : Map<String,String>
    ) : Call<String>

    @GET("playlists")
    suspend fun fetchPlayLists(
        @Query("part") part : String,
        @Query("mine") mine : Boolean,
        @HeaderMap headers : Map<String,String>
    ) : Call<String>

    @GET("activities")
    suspend fun fetchUserActivitiesWithoutHeaders(
        @Query("part") part : String,
        @Query("maxResults") maxResults : Int,
        @Query("mine") mine : Boolean,
        @Query("access_token") accessToken : String,
    ) : Call<String>

    @GET("activities")
    suspend fun fetchActivities(
        @Query("part") part : String,
        @Query("channelId") channelId : String,
        @Query("maxResults") maxResults : Int,
        @Query("key") key : String
    ) : Call<String>


}