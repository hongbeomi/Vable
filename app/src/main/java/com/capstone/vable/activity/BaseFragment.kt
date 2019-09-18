package com.capstone.vable.activity

import android.app.ProgressDialog
import androidx.fragment.app.Fragment

open class BaseFragment : androidx.fragment.app.Fragment() {

  private var dialog: ProgressDialog? = null

  fun showProgress(msg: String) {
    if (dialog == null) {
      dialog = ProgressDialog(this.context)
    }
    dialog?.apply {
      setTitle(msg)
      setMessage("잠시만 기다려주세요...")
      show()
      setCanceledOnTouchOutside(false)
    }
  }

  fun hideProgress() {
    if (dialog != null && dialog!!.isShowing) {
      dialog?.hide()
    }
  }

  override fun onDestroy() {
    dialog?.dismiss()
    super.onDestroy()
  }

}