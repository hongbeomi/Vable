package com.capstone.vable.dto

data class ResponseAccountsDTO(
  var user_id: String, var name: String, var password: String, var gender: String,
  var priority_volunteer: String, var location: String, var sub_location: String
)