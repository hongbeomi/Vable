package com.capstone.vable.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object NetRetrofit {

  private var okHttpClient = OkHttpClient
    .Builder()
    .connectTimeout(60, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(60, TimeUnit.SECONDS)
    .build()

  var retrofit = Retrofit
    .Builder()
    .client(okHttpClient)
//    .baseUrl("http://192.168.10.212:8080")
    .baseUrl("https://capstone-wisoft.ml")
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())!!
}