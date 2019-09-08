package com.capstone.vable.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object NetRetrofit {

  var retrofit = Retrofit
    .Builder()
    .baseUrl("http://192.168.10.212:8080")
//    .baseUrl("http://vable.yep.kr")
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create())!!

}