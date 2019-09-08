package com.capstone.vable.preferencesdata

import android.app.Application

class App : Application() {

  companion object {
    lateinit var prefs: SharedPreferences
  }

  override fun onCreate() {
    prefs = SharedPreferences(applicationContext)
    super.onCreate()
  }

}