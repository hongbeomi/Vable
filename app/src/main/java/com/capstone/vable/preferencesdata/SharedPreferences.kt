package com.capstone.vable.preferencesdata

import android.content.Context
import android.content.SharedPreferences

class SharedPreferences(context: Context) {

  private val p = "prefs"
  private val id = "myKey"
  private val user_id = "myId"
  private val pass = "myPass"
  private val name = "myName"
  private val gender = "myGender"
  private val type = "myType"
  private val local = "myLocal"
  private val subLocal = "mySubLocal"
  private val checkFingerPrint = "myFingerPrintState"
  private val prefs: SharedPreferences? = context.getSharedPreferences(p, 0)

  var myKey: String
    get() = prefs?.getString(id, "").toString()
    set(value) = prefs?.edit()?.putString(id, value)?.apply()!!

  var myId: String
    get() = prefs?.getString(user_id, "").toString()
    set(value) = prefs?.edit()?.putString(user_id, value)?.apply()!!

  var myPass: String
    get() = prefs?.getString(pass, "").toString()
    set(value) = prefs?.edit()?.putString(pass, value)?.apply()!!

  var myName: String
    get() = prefs?.getString(name, "").toString()
    set(value) = prefs?.edit()?.putString(name, value)?.apply()!!

  var myGender: String
    get() = prefs?.getString(gender, "").toString()
    set(value) = prefs?.edit()?.putString(gender, value)?.apply()!!

  var myType: String
    get() = prefs?.getString(type, "").toString()
    set(value) = prefs?.edit()?.putString(type, value)?.apply()!!

  var myLocal: String
    get() = prefs?.getString(local, "").toString()
    set(value) = prefs?.edit()?.putString(local, value)?.apply()!!

  var mySubLocal: String
    get() = prefs?.getString(subLocal, "").toString()
    set(value) = prefs?.edit()?.putString(subLocal, value)?.apply()!!

  var myFingerPrintState: Boolean
  get() = prefs?.getBoolean(checkFingerPrint, true)!!
  set(value) = prefs?.edit()?.putBoolean(checkFingerPrint, value)?.apply()!!

}