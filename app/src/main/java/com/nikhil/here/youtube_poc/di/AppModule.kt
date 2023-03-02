package com.nikhil.here.youtube_poc.di

import com.mocklets.pluto.PlutoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import pub.devrel.easypermissions.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    companion object {
        const val YT_BASE_URL = "https://youtube.googleapis.com/youtube/v3/"
        const val AUTH_BASE_URL = "https://www.googleapis.com/oauth2/v3/"
    }


    @Provides
    @Singleton
    fun provideLoggingInterceptor() = OkHttpClient.Builder()
        .addInterceptor(PlutoInterceptor())
        .build()

    @Provides
    @Singleton
    @Named("youtube")
    fun provideRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(YT_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(AUTH_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    @Provides
    @Singleton
    fun provideApiService(@Named("youtube") retrofit: Retrofit) = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideAuthService(@Named("auth") retrofit: Retrofit) = retrofit.create(AuthService::class.java)


}