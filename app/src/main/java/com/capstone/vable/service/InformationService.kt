package com.capstone.vable.service

import com.capstone.vable.data.LocationData
import com.capstone.vable.dto.ResponseInformationDTO
import retrofit2.Call
import retrofit2.http.*

interface InformationService {

  @FormUrlEncoded
  @PATCH("/account/{id}/")
  fun patchTypeRequest(
    @Path("id") id: String,
    @Field("priority_volunteer") priority_volunteer: String
  ): Call<ResponseInformationDTO>

  @PATCH("/account/{id}/")
  fun patchLocalRequest(
    @Path("id") id: String,
    @Body locationData: LocationData
  ): Call<ResponseInformationDTO>

  @FormUrlEncoded
  @PATCH("/account/{id}/")
  fun patchPasswordRequest(
    @Path("id") id: String,
    @Field("password") password: String
  ): Call<ResponseInformationDTO>

}