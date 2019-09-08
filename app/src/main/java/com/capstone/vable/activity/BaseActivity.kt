package com.capstone.vable.activity

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity
import java.security.MessageDigest


@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

  private var dialog: ProgressDialog? = null

  // 프로그래스 다이얼로그 보여주기
  fun showProgress(msg: String) {
    if (dialog == null) {
      dialog = ProgressDialog(this)
    }
    dialog?.apply {
      setTitle(msg)
      setMessage("잠시만 기다려주세요...")
      show()
      setCanceledOnTouchOutside(false)
    }
  }

  // 프로그래스 다이얼로그 숨김
  fun hideProgress() {
    if (dialog != null && dialog!!.isShowing) {
      dialog?.hide()
    }
  }

  // sha256 알고리즘으로 비밀번호 암호화
  fun sha256(msg: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg.toByteArray())
    val builder = StringBuilder()
    for (b in md.digest()) {
      builder.append(String.format("%02x", b))
    }
    return builder.toString()
  }

  override fun onDestroy() {
    dialog?.dismiss()
    super.onDestroy()
  }

}