package com.geodeveloper.easypay.service

import android.annotation.SuppressLint
import android.os.Build
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit

object ServiceBuilder {
    //url of the server
    private const val URL = "https://sandbox.vtpass.com/api/"
    //test
//    "https://sandbox.vtpass.com/api/"
//    live
//    https://vtpass.com/api/
    //creating logger
    private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)


    //http client
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(logger)

    //create the builder
    private val builder = Retrofit.Builder().baseUrl(URL).addConverterFactory(GsonConverterFactory.create()).client(okHttpClient.build())

    //create instance of retrofit
    private val retrofit = builder.build()

    fun <T> buildService(serviceType: Class<T>): T {
        return retrofit.create(serviceType)
    }

}