package com.capstone.vable.activity

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.view.MenuItem
import com.capstone.vable.fragment.InformationFragment
import com.capstone.vable.R
import com.capstone.vable.controller.BackPressCloseHandler
import com.capstone.vable.data.LocationData
import com.capstone.vable.fragment.RecommendFragment
import com.capstone.vable.fragment.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

  private var backPressCloseHandler: BackPressCloseHandler? = null

  @SuppressLint("PrivateResource")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    backPressCloseHandler = BackPressCloseHandler(this)
    firstNavigationItem()
    bottom_navigation_view.setOnNavigationItemSelectedListener(this)
  }

  // 처음 메인 액티비티 진입 시 추천 프래그먼트 호출
  @SuppressLint("PrivateResource")
  fun firstNavigationItem() {
    val firstFragment = SearchFragment()
    setTransaction(firstFragment)
  }

  // 하단 네비게이션바의 선택에 따라 프래그먼트 호출
  @SuppressLint("PrivateResource")
  override fun onNavigationItemSelected(p0: MenuItem): Boolean {
    when (p0.itemId) {
      R.id.search_menu -> {
        val searchFragment = SearchFragment()
        setTransaction(searchFragment)
      }
      R.id.recommend_menu -> {
        val recommendFragment = RecommendFragment()
        setTransaction(recommendFragment)
      }
      R.id.information_menu -> {
        val informationFragment = InformationFragment()
        setTransaction(informationFragment)
      }
    }
    return true
  }

  // 뒤로가기 버튼
  override fun onBackPressed() {
    backPressCloseHandler?.onBackPressed()
  }

  @SuppressLint("PrivateResource")
  private fun setTransaction(fragment: androidx.fragment.app.Fragment) {
    supportFragmentManager.beginTransaction()
      .setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
      .addToBackStack(null)
      .replace(R.id.frame_layout, fragment)
      .commit()
  }

}
