package com.capstone.vable.fragment

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import com.google.android.material.textfield.TextInputLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.capstone.vable.R
import com.capstone.vable.activity.LoginActivity
import com.capstone.vable.dto.ResponseInformationDTO
import com.capstone.vable.preferencesdata.App
import com.capstone.vable.service.InformationService
import com.capstone.vable.service.NetRetrofit
import kotlinx.android.synthetic.main.information_fragment.view.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import com.capstone.vable.activity.BaseFragment
import com.capstone.vable.data.LocationData
import java.security.MessageDigest
import java.util.regex.Pattern

class InformationFragment : BaseFragment() {

  var passCheck = false
  private var server = NetRetrofit.retrofit
    .build()
    .create(InformationService::class.java)
  private lateinit var builder: AlertDialog.Builder

  @SuppressLint("InflateParams")
  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?, savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.information_fragment, null)
    builder = AlertDialog.Builder(view.context, R.style.MyAlertDialogStyle)
    logOut(view)
    setUserInformation(view)
    updateTypeButtonListener(view)
    updateLocalButtonListener(view)
    updatePasswordButtonListener(view)
    return view
  }

  private fun logOut(view: View) {
    val logOutButton = view.findViewById<Button>(R.id.logOutButton)
    logOutButton?.setOnClickListener {
//      informationClear()
      toast("로그아웃되었습니다.")
      startActivity<LoginActivity>()
    }
  }

  private fun updateTypeInformation(view: View, type: String) {
    showProgress("봉사영역 변경")
    server.patchTypeRequest(App.prefs.myKey, type)
      .enqueue(object : Callback<ResponseInformationDTO> {
        override fun onFailure(call: Call<ResponseInformationDTO>, t: Throwable) {
          Log.e("fail", "봉사종류 변경 통신 실패")
          t.printStackTrace()
          toast("통신 실패")
          hideProgress()
        }

        override fun onResponse(
          call: Call<ResponseInformationDTO>,
          response: Response<ResponseInformationDTO>
        ) {
          try {
            toast("변경되었습니다")
            setUserInformation(view)
          } catch (e: Exception) {
            e.printStackTrace()
            toast("사용자 정보 불러오기 실패")
          } finally {
            hideProgress()
          }
        }
      })
  }

  private fun updateLocalInformation(view: View, local: String, subLocal: String) {
    showProgress("지역 변경")
    val locationData = LocationData()
    locationData.location = local
    locationData.sub_location = subLocal

    server.patchLocalRequest(App.prefs.myKey, locationData)
      .enqueue(object : Callback<ResponseInformationDTO> {
        override fun onFailure(call: Call<ResponseInformationDTO>, t: Throwable) {
          Log.e("fail", "지역 변경 통신 실패")
          t.printStackTrace()
          toast("통신 실패")
          hideProgress()
        }

        override fun onResponse(
          call: Call<ResponseInformationDTO>,
          response: Response<ResponseInformationDTO>
        ) {
          try {
            toast("변경되었습니다")
            setUserInformation(view)
          } catch (e: Exception) {
            e.printStackTrace()
            toast("사용자 정보 불러오기 실패")
          } finally {
            hideProgress()
          }
        }
      })
  }

  private fun updatePasswordInformation(password: String) {
    showProgress("비밀번호 변경")
    server.patchPasswordRequest(App.prefs.myKey, password)
      .enqueue(object : Callback<ResponseInformationDTO> {
        override fun onFailure(call: Call<ResponseInformationDTO>, t: Throwable) {
          Log.e("fail", "비밀번호 변경 통신 실패")
          t.printStackTrace()
          toast("통신 실패")
          hideProgress()
        }

        override fun onResponse(
          call: Call<ResponseInformationDTO>,
          response: Response<ResponseInformationDTO>
        ) {
          try {
            println(response.body()?.password.toString().trim())
            App.prefs.myPass = response.body()?.password.toString().trim()
            toast("변경되었습니다")
          } catch (e: Exception) {
            e.printStackTrace()
            toast("비밀번호 변경 실패")
          } finally {
            hideProgress()
          }
        }
      })
  }

  @SuppressLint("InflateParams")
  private fun updateTypeButtonListener(view: View) {
    val changeTypeButton = view.findViewById<Button>(R.id.changeTypeButton)
    changeTypeButton?.setOnClickListener {
      val mView = layoutInflater.inflate(R.layout.alert_type, null)
      builder.setTitle("선호하는 봉사활동 영역을 선택해주세요")
      val typeSpinner = mView.findViewById(R.id.typeChangeSpinner) as Spinner
      val typeAdapter = ArrayAdapter.createFromResource(
        mView.context, R.array.봉사종류,
        R.layout.support_simple_spinner_dropdown_item
      )
      typeAdapter.setDropDownViewResource(R.layout.dropdown_item)
      typeSpinner.adapter = typeAdapter

      builder.setPositiveButton("변경하기") { dialog: DialogInterface?, _ ->
        if (typeSpinner.selectedItem.toString() != "") {
          App.prefs.myType = typeSpinner.selectedItem.toString()
          updateTypeInformation(view, App.prefs.myType)
          setUserInformation(view)
          dialog?.dismiss()
        } else {
          toast("변경 실패")
          dialog?.dismiss()
        }
      }
      setDialogNegativeListener()
      setShowDialog(mView)
    }
  }

  @SuppressLint("InflateParams")
  private fun updateLocalButtonListener(view: View) {
    val changeLocalButton = view.findViewById<Button>(R.id.changeLocalAllButton)

    changeLocalButton?.setOnClickListener {
      val mView = layoutInflater.inflate(R.layout.alert_local, null)
      builder.setTitle("변경할 지역을 선택해주세요")
      val localSpinner = mView.findViewById(R.id.localChangeSpinner) as Spinner
      val subLocalSpinner = mView.findViewById(R.id.subLocalChangeSpinner) as Spinner

      val localAdapter = ArrayAdapter.createFromResource(
        mView.context, R.array.지역,
        R.layout.support_simple_spinner_dropdown_item
      )
      localAdapter.setDropDownViewResource(R.layout.dropdown_item)
      localSpinner.adapter = localAdapter
      localSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
          val localSelector = localAdapter.getItem(position)?.toString()
          val textArrayResId = resources.getIdentifier(
            "array/$localSelector",
            "name", activity?.packageName
          )
          val subLocalAdapter = ArrayAdapter.createFromResource(
            mView.context,
            textArrayResId, android.R.layout.simple_spinner_dropdown_item
          )
          subLocalAdapter.setDropDownViewResource(R.layout.dropdown_item)
          subLocalSpinner.adapter = subLocalAdapter
        }
        override fun onNothingSelected(parent: AdapterView<*>?) {}
      }
      builder.setPositiveButton("변경하기") { dialog: DialogInterface?, _ ->
        if (localSpinner.selectedItem.toString() != "" &&
          subLocalSpinner.selectedItem.toString() != ""
        ) {
          App.prefs.apply {
            myLocal = localSpinner.selectedItem.toString()
            mySubLocal = subLocalSpinner.selectedItem.toString()
          }
          updateLocalInformation(view, App.prefs.myLocal, App.prefs.mySubLocal)
          setUserInformation(view)
          dialog?.dismiss()
        } else {
          toast("변경 실패")
          dialog?.dismiss()
        }
      }
      setDialogNegativeListener()
      setShowDialog(mView)
    }
  }

  @SuppressLint("InflateParams")
  private fun updatePasswordButtonListener(view: View) {
    val changePasswordButton = view.findViewById<Button>(R.id.changePasswordButton)

    changePasswordButton?.setOnClickListener {
      val mView = layoutInflater.inflate(R.layout.alert_password, null)
      val changePasswordEditText = mView.findViewById<EditText>(R.id.changePasswordEditText)
      val changePasswordLayout = mView.findViewById<TextInputLayout>(R.id.changePasswordLayout)

      builder.apply {
        setTitle("변경할 비밀번호를 입력해주세요")
        setPositiveButton("변경하기") { _, _ -> }
      }
      setDialogNegativeListener()
      builder.setView(mView)
      val dialog = builder.create()
      dialog.show()
      val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

      changePasswordEditText?.addTextChangedListener(object : TextWatcher {
        private val checkKey: String =
          "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[\$@\$!%*#?&])[A-Za-z\\d\$@\$!%*#?&]{8,16}\$"

        override fun afterTextChanged(s: Editable?) {
          if (!Pattern.matches(checkKey, changePasswordEditText.text.toString()) ||
            changePasswordEditText.text.toString() == ""
          ) {
            changePasswordLayout.apply {
              error = null
              error = "영어, 숫자, 특수기호 8~16자 입력"
            }
            passCheck = false
            positiveButton.isEnabled = false
          } else {
            changePasswordLayout.error = null
            passCheck = true
            positiveButton.apply {
              isEnabled = true
              setOnClickListener {
                App.prefs.myPass = sha256(changePasswordEditText.text.toString().trim())
                updatePasswordInformation(App.prefs.myPass)
                dialog.dismiss()
              }
            }
          }
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
      })
    }
  }

  private fun sha256(msg: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    md.update(msg.toByteArray())
    val builder = StringBuilder()
    for (b in md.digest()) {
      builder.append(String.format("%02x", b))
    }
    return builder.toString()
  }

  private fun informationClear() {
    App.prefs.apply {
      myKey = ""
      myId = ""
      myPass = ""
      myGender = ""
      myType = ""
      myLocal = ""
      mySubLocal = ""
    }
  }

  private fun setUserInformation(view: View) {
    view.informationIdTextView.text = App.prefs.myId
    view.informationNameTextView.text = App.prefs.myName
    view.informationNameSubTextView.text = App.prefs.myName
    view.informationTypeTextView.text = App.prefs.myType
    view.informationLocalTextView.text = App.prefs.myLocal
    view.informationSubLocalTextView.text = App.prefs.mySubLocal
  }

  private fun setDialogNegativeListener() {
    builder.setNegativeButton("취소") { dialog: DialogInterface?, _ ->
      dialog?.dismiss()
    }
  }

  private fun setShowDialog(mView: View) {
    builder.setView(mView)
    val dialog = builder.create()
    dialog.show()
  }

}