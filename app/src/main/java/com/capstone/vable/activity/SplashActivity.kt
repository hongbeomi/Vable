package com.capstone.vable.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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