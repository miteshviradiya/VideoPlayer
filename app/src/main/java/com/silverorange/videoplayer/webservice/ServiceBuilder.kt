package com.silverorange.videoplayer.webservice

import com.silverorange.videoplayer.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    private const val URL = Constant.BaseUrl

    // Create Logger -> used to print response
    private val logger = run {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.apply {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // OkHttp Client for API call
    private val okHttp = OkHttpClient.Builder()
        .callTimeout(240, TimeUnit.SECONDS)
        .addInterceptor(logger)

    // Create Retrofit Builder -> currently fetching response in string only so use scalar
    private val builder_string = Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory(ScalarsConverterFactory.create()) // for getting response in string
        .client(okHttp.build())

    // Create Retrofit Instance
    private val retrofit_string = builder_string.build()

    fun <T> buildStringService(serviceType: Class<T>): T {
        return retrofit_string.create(serviceType)
    }

}