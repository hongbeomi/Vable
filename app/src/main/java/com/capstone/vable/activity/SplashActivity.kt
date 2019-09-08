package com.capstone.vable.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.annotation.SuppressLint
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

  @SuppressLint("PrivateResource")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    startActivity<LoginActivity>()
    finish()
  }

}