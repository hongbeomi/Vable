package com.capstone.vable.controller

import android.app.Activity
import android.widget.Toast

class BackPressCloseHandler(private val activity: Activity) {

  private var backKeyPressedTime: Long = 0
  // 뒤로가기 버튼을 클릭했을때 시간 차이를 주고 그에 따라 행동
  fun onBackPressed() {
    if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
      backKeyPressedTime = System.currentTimeMillis()
      Toast.makeText(
        activity, "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.",
        Toast.LENGTH_SHORT
      ).show()
      return
    }
    if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
      activity.finish()
    }
  }

}