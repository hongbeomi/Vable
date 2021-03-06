package com.capstone.vable.service

import com.capstone.vable.dto.ResponseVolunteersDTO
import retrofit2.Call
import retrofit2.http.*

interface VolunteersService {

  @GET("/all/")
  fun getRequest(): Call<List<ResponseVolunteersDTO>>

  @GET("/search/")
  fun getParamRequest(
    @Query("title") title: String
  ): Call<List<ResponseVolunteersDTO>>

  @GET("/recommend/")
  fun getRecommendRequest(
    @QueryMap option : Map<String, String>
//    @Query("gender") gender: String,
//    @Query("location") location: String
  ): Call<List<ResponseVolunteersDTO>>

  @POST("/click/")
  fun postClickRequest(
    @Body title: String
  ): Call<String>

}