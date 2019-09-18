package com.capstone.vable.controller

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor


class MainThreadExecutor : Executor {

  // 메인 루퍼에서 핸들러를 가져옴
  private val handler = Handler(Looper.getMainLooper())

  override fun execute(r: Runnable) {
    handler.post(r)
  }

}