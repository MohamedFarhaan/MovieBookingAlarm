package com.example.moviebookingalarm.service.network

import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.IOException
import retrofit2.converter.scalars.ScalarsConverterFactory;

interface InoxService {

    @POST("TestQuickBookingHandler.ashx")
    suspend fun getMoviesList(@Query("GetMovieList") GetMovieList:String = "True",
                              @Query("CinemaID") CinemaID: Int = 0): retrofit2.Response<String>;

    @POST("ScheduleCinemaHandler_Cache.ashx")
    suspend fun getScheduledMoviesList(
        @Query("ShowDate") ShowDate: String,
        @Query("CityID") CityID: Int,
        @Query("Showday") Showday:String = "True",
        @Query("PageIndex") PageIndex: Int = 1): retrofit2.Response<String>;

}

