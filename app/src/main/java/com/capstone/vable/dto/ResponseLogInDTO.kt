package com.capstone.vable.dto

data class ResponseLogInDTO(
  var id: String,
  var user_id: String, var password: String, var name: String,
  var gender: String, var priority_volunteer: String,
  var location: String, var sub_location: String
)