package com.capstone.vable.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import com.capstone.vable.*
import com.capstone.vable.controller.BackPressCloseHandler
import com.capstone.vable.dto.ResponseLogInDTO
import com.capstone.vable.service.NetRetrofit
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.capstone.vable.preferencesdata.App
import com.capstone.vable.service.LogInService
import androidx.biometric.BiometricConstants.ERROR_NEGATIVE_BUTTON
import com.capstone.vable.controller.MainThreadExecutor


class LoginActivity : BaseActivity() {

  private var server = NetRetrofit.retrofit
    .build()
    .create(LogInService::class.java)
  private var backPressCloseHandler: BackPressCloseHandler? = null
  private var fingerId: String = ""
  private var fingerPassword: String = ""

  private var biometricPrompt: BiometricPrompt? = null
  private val executor = MainThreadExecutor()


  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_login)
    backPressCloseHandler = BackPressCloseHandler(this)

//    if (App.prefs.myId != "") {
//      startActivity<MainActivity>()
//      finish()
//    }
    if (biometricPrompt == null) {
      biometricPrompt = BiometricPrompt(this, executor, callback)
    }

    fingerId = App.prefs.myId
    fingerPassword = App.prefs.myPass
    if (fingerId != "" && fingerPassword != "") {
      val promptInfo = buildBiometricPrompt()
      biometricPrompt!!.authenticate(promptInfo)
    }
    goRegister()
    logIn()
  }

  // 지문 인지 콜백 함수
  private val callback = object : BiometricPrompt.AuthenticationCallback() {
    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
      super.onAuthenticationError(errorCode, errString)
      if (errorCode == ERROR_NEGATIVE_BUTTON && biometricPrompt != null)
        biometricPrompt!!.cancelAuthentication()

    }

    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
      super.onAuthenticationSucceeded(result)
      logInIdInputEditText.setText(fingerId)
      logInPassInputEditText.setText(fingerPassword)

    }

    override fun onAuthenticationFailed() {
      super.onAuthenticationFailed()
    }
  }

  // 지문인식 로그인
  private fun buildBiometricPrompt(): BiometricPrompt.PromptInfo {
      return BiometricPrompt.PromptInfo.Builder()
        .setTitle("지문 로그인")
        .setDescription("손가락을 센서에 올려주세요.")
        .setNegativeButtonText("취소")
        .build()
  }

  // 로그인 구현
  private fun getCheckInformation(query: String) {
    showProgress("로그인")
    server.getLoginRequest(query).enqueue(object : Callback<List<ResponseLogInDTO>> {
      override fun onFailure(call: Call<List<ResponseLogInDTO>>, t: Throwable) {
        Log.e("fail", "로그인 통신 실패")
        t.printStackTrace()
        toast("통신 실패")
        hideProgress()
      }

      override fun onResponse(
        call: Call<List<ResponseLogInDTO>>,
        response: Response<List<ResponseLogInDTO>>
      ) {
        try {
          if (response.body()?.get(0)?.password.toString().trim() ==
            sha256(logInPassInputEditText.text.toString().trim())
          ) {
            toast("로그인되었습니다.")
            App.prefs.apply {
              myKey = response.body()?.get(0)?.id.toString().trim()
              myId = response.body()?.get(0)?.user_id.toString().trim()
//              myPass = response.body()?.get(0)?.password.toString().trim()
              myPass = logInPassInputEditText.text.toString()
              myName = response.body()?.get(0)?.name.toString().trim()
              myGender = response.body()?.get(0)?.gender.toString().trim()
              myType = response.body()?.get(0)?.priority_volunteer.toString().trim()
              myLocal = response.body()?.get(0)?.location.toString().trim()
              mySubLocal = response.body()?.get(0)?.sub_location.toString().trim()
            }
            startActivity<MainActivity>()
          } else {
            println(response.body()?.get(0)?.password.toString().trim())
            toast("비밀번호가 일치하지 않습니다.")
          }
        } catch (e: Exception) {
          e.printStackTrace()
          toast("존재하지 않는 아이디입니다.")
        } finally {
          hideProgress()
        }
      }
    })
  }

  // 회원가입화면으로 향하는 버튼 리스너 구현
  private fun goRegister() {
    registerButton.setOnClickListener {
      startActivity<RegisterActivity>()
      overridePendingTransition(
        R.anim.anim_slide_in_right,
        R.anim.anim_slide_out_left
      )
    }
  }

  @SuppressLint("PrivateResource")
  private fun logIn() {
    logInButton.setBackgroundResource(R.color.mtrl_btn_bg_color_disabled)
    textWatcher()
    logInButton.setOnClickListener {
      when {
        logInIdInputEditText.text.toString() == "" -> {
          toast("아이디를 입력하세요")
        }
        logInPassInputEditText.text.toString() == "" -> {
          toast("비밀번호를 입력하세요")
        }
        else -> {
          getCheckInformation(logInIdInputEditText.text.toString())
        }
      }
    }
  }

  // 입력되는 값을 계속 지켜보면서 로그인 버튼 색을 변화 시킴
  private fun textWatcher() {
    logInIdInputEditText.addTextChangedListener(object : TextWatcher {
      @SuppressLint("PrivateResource")
      override fun afterTextChanged(s: Editable?) {
        checkLoginDataIsNull()
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })

    logInPassInputEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        checkLoginDataIsNull()
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }

  // 아이디, 비밀번호에 대한 값의 길이에 따라 로그인 버튼 색 변화
  @SuppressLint("ResourceAsColor", "PrivateResource")
  private fun checkLoginDataIsNull() {
    if (logInIdInputEditText.text.toString() == ""
      || logInPassInputEditText.text.toString() == ""
    ) {
      logInButton.setBackgroundResource(R.color.mtrl_btn_bg_color_disabled)
    } else {
      logInButton.setBackgroundResource(R.drawable.ripple_custom_login_button)
    }
  }

  // 뒤로가기 버튼
  override fun onBackPressed() {
    backPressCloseHandler?.onBackPressed()
  }

}
