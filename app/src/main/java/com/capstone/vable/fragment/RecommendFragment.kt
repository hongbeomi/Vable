package com.capstone.vable.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.capstone.vable.*
import com.capstone.vable.activity.BaseFragment
import com.capstone.vable.adapter.RecommendRecyclerAdapter
import com.capstone.vable.data.RecommendItem
import com.capstone.vable.dto.ResponseVolunteersDTO
import com.capstone.vable.preferencesdata.App
import com.capstone.vable.service.NetRetrofit
import com.capstone.vable.service.VolunteersService
import org.jetbrains.anko.support.v4.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList

class RecommendFragment : BaseFragment() {

  private var server = NetRetrofit.retrofit
    .build()
    .create(VolunteersService::class.java)
  var oldRecommendList = arrayListOf<RecommendItem>()
  private var recyclerAdapter = RecommendRecyclerAdapter(oldRecommendList)

  @SuppressLint("InflateParams")
  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.recommend_fragment, null)
    requestSwipeRefresh(view)
    return view
  }

  private fun requestSwipeRefresh(view: View) {
    val recyclerView = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.recommendRecyclerView)
    val linearLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
    recyclerView.layoutManager = linearLayoutManager
    recyclerView.adapter = recyclerAdapter
    setRecommendName(view)
    val swipeRefreshLayout = view.findViewById<androidx.swiperefreshlayout.widget.SwipeRefreshLayout>(R.id.swipeLayout)
    swipeRefreshLayout.setOnRefreshListener {
      Handler().postDelayed({
        swipeRefreshLayout.isRefreshing = false
        getRecommendVolunteerInformation()
      }, 1000)
    }
    getRecommendVolunteerInformation()
  }

  // TODO 추천 요청으로 바꿔야됨 !! SharedPreferences 활용!
  private fun getRecommendVolunteerInformation() {
    server.getRequest().enqueue(object : Callback<List<ResponseVolunteersDTO>> {
      override fun onFailure(call: Call<List<ResponseVolunteersDTO>>, t: Throwable) {
        Log.e("fail", "봉사정보 불러오기 실패!")
        t.printStackTrace()
        toast("통신 실패")
        hideProgress()
      }

      override fun onResponse(
        call: Call<List<ResponseVolunteersDTO>>,
        response: Response<List<ResponseVolunteersDTO>>
      ) {
        try {
          val size = response.body()?.size?.minus(1)
          val newRecommendList = ArrayList<RecommendItem>()
          for (i in 0..size!!) {
            newRecommendList.add(
              RecommendItem(
                response.body()?.get(i)?.title.toString(),
                response.body()?.get(i)?.gender.toString(),
                response.body()?.get(i)?.location.toString(),
                response.body()?.get(i)?.sub_location.toString(),
                response.body()?.get(i)?.contents.toString()
              )
            )
          }
          oldRecommendList.clear()
          oldRecommendList.addAll(newRecommendList)
          recyclerAdapter.notifyDataSetChanged()
        } catch (e: Exception) {
          e.printStackTrace()
          toast("봉사활동 추천 오류")
          getRecommendVolunteerInformation()
        }
      }
    })
  }

  private fun setRecommendName(view: View) {
    val recommendNameEditText = view.findViewById<TextView>(R.id.recommendNameEditText)
    recommendNameEditText.text = App.prefs.myName
  }

}