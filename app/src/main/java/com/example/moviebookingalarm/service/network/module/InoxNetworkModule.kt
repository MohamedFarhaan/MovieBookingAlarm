package com.example.moviebookingalarm.service.network.module

import com.example.moviebookingalarm.service.network.InoxService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.io.IOException
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InoxNetworkModule {
    @Singleton
    @Provides
    fun provideRetrofitService(): InoxService {
        return Retrofit.Builder()
            .baseUrl("https://www.inoxmovies.com/Handlers/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor(HeaderInterceptor()).build())
            .build().create<InoxService>()
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