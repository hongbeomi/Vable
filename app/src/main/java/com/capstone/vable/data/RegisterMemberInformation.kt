package com.capstone.vable.data

data class RegisterMemberInformation(
  var name: String = "", var user_id: String = "",
  var password: String = "", var gender: String = "",
  var type: String = "", var location: String = "",
  var sub_location: String = ""
)