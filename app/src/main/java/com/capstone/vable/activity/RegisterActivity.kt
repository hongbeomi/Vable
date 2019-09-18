package com.capstone.vable.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.capstone.vable.*
import com.capstone.vable.data.RegisterMemberInformation
import com.capstone.vable.dto.ResponseAccountsDTO
import com.capstone.vable.service.AccountsService
import com.capstone.vable.service.NetRetrofit
import kotlinx.android.synthetic.main.activity_register.*
import org.jetbrains.anko.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.regex.Pattern

class RegisterActivity : BaseActivity() {

  var registerInformation = RegisterMemberInformation()
  private var server = NetRetrofit.retrofit
    .build()
    .create(AccountsService::class.java)
  var nameCheck = false
  var emailCheck = false
  var passCheck = false
  var passConfirmCheck = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_register)
    backToLogInButton()
    setVolunteerTypeSave()
    setLocalSpinnerSave()
    setGenderSave()
    saveRegisterInformation()
  }

  // 회원저장 기능
  private fun saveAccounts() {
    showProgress("회원가입")
    server.postRequest(registerInformation).enqueue(object : Callback<ResponseAccountsDTO> {
      override fun onFailure(call: Call<ResponseAccountsDTO>, t: Throwable) {
        Log.e("fail", "회원정보 전송 실패")
        t.printStackTrace()
        toast("통신 실패")
        hideProgress()
      }

      override fun onResponse(call: Call<ResponseAccountsDTO>
                              , response: Response<ResponseAccountsDTO>) {
        try {
          println(response.body()?.user_id.toString())
        } catch (e: Exception) {
          e.printStackTrace()
          toast("회원정보 저장 실패")
        } finally {
          hideProgress()
        }
      }
    })
  }

  // 봉사활동 영역 선택 및 저장
  private fun setVolunteerTypeSave() {
    val typeAdapter = ArrayAdapter.createFromResource(
      this@RegisterActivity, R.array.봉사종류,
      R.layout.support_simple_spinner_dropdown_item
    )
    typeSpinner.adapter = typeAdapter
    typeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onNothingSelected(parent: AdapterView<*>?) {}
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        registerInformation.priority_volunteer = typeAdapter.getItem(position)!!.toString()
      }
    }
  }

  // 대도시 선택 후 그에 따른 소도시 스피너 선택 구현
  private fun setLocalSpinnerSave() {
    val adapter1 = ArrayAdapter.createFromResource(
      this@RegisterActivity, R.array.지역,
      R.layout.support_simple_spinner_dropdown_item
    )

    bigLocalSpinner.adapter = adapter1
    bigLocalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val localSelector = adapter1.getItem(position)!!.toString()
        val textArrayResId = resources.getIdentifier(
          "array/$localSelector",
          "name", packageName
        )
        registerInformation.location = localSelector

        val adapter2 = ArrayAdapter.createFromResource(
          this@RegisterActivity,
          textArrayResId, android.R.layout.simple_spinner_dropdown_item
        )
        smallLocalSpinner.adapter = adapter2
        smallLocalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
          override fun onItemSelected(parent: AdapterView<*>?, view: View?,
                                      position: Int, id: Long) {
            registerInformation.sub_location = adapter2.getItem(position)!!.toString()
          }
          override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
      }
      override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
  }

  // 성별 선택에 따른 저장 기능
  private fun setGenderSave() {
    genderGroup.setOnCheckedChangeListener { _, i ->
      checkRegisterDataIsNull()
      when (i) {
        maleButton.id -> registerInformation.gender = "남"
        femaleButton.id -> registerInformation.gender = "여"
      }
    }
  }

  // 회원가입 버튼
  @SuppressLint("ResourceAsColor")
  private fun saveRegisterInformation() {
    watcher()
    registerSaveButton.setOnClickListener {
      registerInformation.apply {
        name = registerNameEditText.text.toString()
        user_id = registerIdEditText.text.toString()
        password = sha256(registerPasswordEditText.text.toString())
      }
      saveAccounts()
      startActivity<LoginActivity>()
    }
  }

  // 로그인 버튼으로 나가기
  private fun backToLogInButton() {
    registerBackButton.setOnClickListener {
      startActivity<LoginActivity>()
      overridePendingTransition(
        R.anim.anim_slide_in_left,
        R.anim.anim_slide_out_right
      )
    }
  }

  // 실시간으로 텍스트 입력 결과에 따라 저장 버튼의 색이 변함
  private fun watcher() {
    registerNameEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        if (registerNameEditText.text!!.isEmpty()) {
          registerNameLayout.error = "이름을 입력해주세요"
          nameCheck = false
        } else {
          registerPasswordCheckLayout.error = null
          registerNameLayout.error = null
          nameCheck = true
        }
        checkRegisterDataIsNull()
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
    registerIdEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(registerIdEditText.text.toString())
            .matches()
        ) {
          registerPasswordCheckLayout.error = null
          registerEmailLayout.error = "이메일 형식으로 입력해주세요"
          emailCheck = false
        } else {
          registerEmailLayout.error = null
          emailCheck = true
        }
        checkRegisterDataIsNull()
      }
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })

    registerPasswordEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        if (!Pattern.matches(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\$@\$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,16}\$",
            registerPasswordEditText.text.toString())) {
          registerPasswordLayout.error = null
          registerPasswordLayout.error = "영어, 숫자, 특수기호 8~16자 입력"
          passCheck = false
        } else {
          registerPasswordLayout.error = null
          passCheck = true
        }
        checkRegisterDataIsNull()
      }
      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })

    registerPasswordCheckEditText.addTextChangedListener(object : TextWatcher {
      override fun afterTextChanged(s: Editable?) {
        if (registerPasswordEditText.text.toString() !=
          registerPasswordCheckEditText.text.toString()
        ) {
          registerPasswordCheckLayout.error = null
          registerPasswordCheckLayout.error = "비밀번호가 일치하지 않습니다"
          passConfirmCheck = false
        } else {
          registerPasswordCheckLayout.error = null
          passConfirmCheck = true
        }
        checkRegisterDataIsNull()
      }

      override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
      override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
  }

  // 입력된 값의 유무에 따라 저장 버튼 색 변화
  private fun checkRegisterDataIsNull() {
    if (!nameCheck || !emailCheck ||
      !passCheck || !passConfirmCheck ||
      (!maleButton.isChecked && !femaleButton.isChecked)) {
      registerSaveButton.apply {
        setTextColor(ContextCompat.getColor(applicationContext, R.color.disable))
        isEnabled = false
        invalidate()
      }
    } else {
      registerSaveButton.apply {
        setTextColor(ContextCompat.getColor(applicationContext, R.color.purple))
        isEnabled = true
        invalidate()
      }
    }
  }

  override fun onBackPressed() {
    startActivity<LoginActivity>()
    overridePendingTransition(
      R.anim.anim_slide_in_left,
      R.anim.anim_slide_out_right
    )
    super.onBackPressed()
  }

}