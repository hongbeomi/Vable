package com.capstone.vable.dto

data class ResponseVolunteersDTO(
  var title: String, var contents: String, var gender: String,
  var location: String, var sub_location: String, val type: String
)