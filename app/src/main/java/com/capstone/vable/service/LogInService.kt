package com.capstone.vable.service

import com.capstone.vable.dto.ResponseLogInDTO
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface LogInService {

  @GET("/account/")
  fun getLoginRequest(
    @Query("user_id") id: String
  ): Call<List<ResponseLogInDTO>>

}