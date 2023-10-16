package com.example.moviebookingalarm.service

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

    companion object {
        var inoxService: InoxService? = null;


        fun getInoxServiceInstance(): InoxService {
            if(inoxService == null) {
                val inst = Retrofit.Builder()
                    .baseUrl("https://www.inoxmovies.com/Handlers/")
//                    .addConverterFactory(GsonConverterFactory.create(Gson().newBuilder().setLenient().create()))
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .client(OkHttpClient.Builder().addInterceptor(HeaderInterceptor()).build())
                    .build().create<InoxService>()
                inoxService = inst;
                return inst;
            } else {
                return inoxService!!;
            }
        }
    }
}

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        // Add your headers here
        val modifiedRequest: Request = originalRequest.newBuilder()
            .header("authority", "www.inoxmovies.com")
            .header("accept", "*/*")
            .header("accept-language", "en-US,en;q=0.9,ta;q=0.8")
            .header("content-length", "0")
            .header("cookie", "ext_name=ojplmecpdpgccookcobabopnaifgidhf; ASP.NET_SessionId=gfcgtoqhr2ai2xf5rxnggyoo; __AntiXsrfToken=92f5b96823464f75a8a372bdc36cfad0; _gid=GA1.2.1945519262.1697435617; i_cc=lPBu1OoRIbaGvu3BqfoSK90PEPPOpNsy3SL4+sPvysM=; tSs=110; _ga_FYEYSCFLNJ=GS1.1.1697435617.2.1.1697435703.38.0.0; _ga=GA1.2.800861931.1694779645")
            .header("dnt", "1")
            .header("origin", "https://www.inoxmovies.com")
            .header("referer", "https://www.inoxmovies.com/")
            .header("sec-ch-ua", "\"Chromium\";v=\"116\", \"Not)A;Brand\";v=\"24\", \"Google Chrome\";v=\"116\"")
            .header("sec-ch-ua-mobile", "?0")
            .header("sec-ch-ua-platform", "\"macOS\"")
            .header("sec-fetch-dest", "empty")
            .header("sec-fetch-mode", "cors")
            .header("sec-fetch-site", "same-origin")
            .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36")
            .header("x-requested-with", "XMLHttpRequest")
            .build()
        return chain.proceed(modifiedRequest)
    }
}