package com.capstone.vable.service

import com.capstone.vable.data.RegisterMemberInformation
import com.capstone.vable.dto.ResponseAccountsDTO
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.POST

interface AccountsService {

  @POST("/account/")
  fun postRequest(
    @Body data: RegisterMemberInformation
  ): Call<ResponseAccountsDTO>

}