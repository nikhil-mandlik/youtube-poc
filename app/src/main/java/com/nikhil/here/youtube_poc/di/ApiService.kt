package com.nikhil.here.youtube_poc.di

import com.nikhil.here.youtube_poc.data.PlayListResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Query

interface ApiService {

    @GET("playlists")
    suspend fun fetchUserPlaylists(
        @Query("part") part : String,
        @Query("mine") mine : Boolean,
        @Query("maxResults") maxResults : Int,
        @HeaderMap headers : Map<String,String>
    ) : Response<PlayListResponse>

    @GET("playlists")
    suspend fun fetchDefaultPlayLists(
        @Query("part") part : String,
        @Query("channelId") channelId : String,
        @Query("key") key : String,
        @Query("maxResults") maxResults : Int,
    ) : Response<PlayListResponse>

}