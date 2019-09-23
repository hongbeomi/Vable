package com.capstone.vable.fragment

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.SearchView
import android.util.Log
import com.capstone.vable.dto.ResponseVolunteersDTO
import com.capstone.vable.data.SearchItem
import com.capstone.vable.service.NetRetrofit
import com.capstone.vable.service.VolunteersService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.android.synthetic.main.search_fragment.*
import android.view.*
import com.capstone.vable.R
import org.jetbrains.anko.toast
import android.content.Context.INPUT_METHOD_SERVICE
import android.view.inputmethod.InputMethodManager
import com.capstone.vable.activity.BaseFragment
import com.capstone.vable.adapter.SearchRecyclerAdapter
import org.jetbrains.anko.act

class SearchFragment : BaseFragment(), SearchView.OnQueryTextListener {

  private var server = NetRetrofit.retrofit
    .build()
    .create(VolunteersService::class.java)
  val searchList = arrayListOf<SearchItem>()
  private var recyclerAdapter = SearchRecyclerAdapter(searchList)

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?
    , savedInstanceState: Bundle?
  ): View? {
    val view = inflater.inflate(R.layout.search_fragment, null)
    val recyclerView = view.findViewById<RecyclerView>(R.id.searchRecyclerView)
    val linearLayoutManager = LinearLayoutManager(context)
    recyclerView.apply {
      layoutManager = linearLayoutManager
      adapter = recyclerAdapter
    }

    val searchView = view.findViewById<SearchView>(R.id.searchView)
    searchView?.setOnQueryTextListener(this)
    getSearchInformation()
    return view
  }

  // 봉사활동 목록 전체 검색
  private fun getSearchInformation() {
    showProgress("봉사활동 정보 읽어오는 중...")
    server.getRequest().enqueue(object : Callback<List<ResponseVolunteersDTO>> {
      override fun onFailure(call: Call<List<ResponseVolunteersDTO>>, t: Throwable) {
        Log.e("fail", "봉사정보 불러오기 실패!")
        t.printStackTrace()
        activity?.toast("통신 실패")
        hideProgress()
      }

      override fun onResponse(
        call: Call<List<ResponseVolunteersDTO>>,
        response: Response<List<ResponseVolunteersDTO>>
      ) {
        if (response.isSuccessful) {
          try {
//            val size = response.body()?.size?.minus(1)
            for (i in 0..20) {
              addSearchList(i, response)
            }
            recyclerAdapter.notifyDataSetChanged()
          } catch (e: Exception) {
            e.printStackTrace()
            activity?.toast("봉사 정보 불러오기 오류")
          } finally {
            hideProgress()
          }
        } else {
          activity?.toast("통신 실패")
          hideProgress()
        }
      }
    })
  }

  // 봉사활동 키워드 검색
  private fun getSearchVolunteerInformation(query: String) {
    showProgress("검색 중...")
    server.getParamRequest(query.trim()).enqueue(object : Callback<List<ResponseVolunteersDTO>> {
      override fun onFailure(call: Call<List<ResponseVolunteersDTO>>, t: Throwable) {
        Log.e("fail", "봉사정보 검색 실패!")
        t.printStackTrace()
        activity?.toast("통신 실패")
        hideProgress()
      }

      override fun onResponse(
        call: Call<List<ResponseVolunteersDTO>>,
        response: Response<List<ResponseVolunteersDTO>>
      ) {
        if (response.isSuccessful) {
          try {
            val size = response.body()?.size?.minus(1)
            searchList.clear()
            for (i in 0..size!!) {
              addSearchList(i, response)
              recyclerAdapter.notifyDataSetChanged()
            }
          } catch (e: Exception) {
            activity?.toast("검색 결과가 존재하지 않습니다.")
            getSearchInformation()
          } finally {
            hideProgress()
          }
        }
        else if (response.code() == 500) {
          activity?.toast("검색 결과가 없습니다.")
          hideProgress()
        }
        else {
          activity?.toast("통신 실패")
          hideProgress()
        }
      }
    })
  }

  // 검색 리스트 불러오기
  private fun addSearchList(i: Int, response: Response<List<ResponseVolunteersDTO>>) {
    searchList.add(
      SearchItem(
        response.body()?.get(i)?.title.toString(),
        response.body()?.get(i)?.gender.toString(),
        response.body()?.get(i)?.location.toString(),
        response.body()?.get(i)?.sub_location.toString(),
        response.body()?.get(i)?.contents.toString().replace(" ", "\u00A0")
      )
    )
    println(response.body()?.get(i)?.title.toString())
  }

  // 쿼리 제출 버튼 클릭 시 반응
  override fun onQueryTextSubmit(p0: String?): Boolean {
    searchList.clear()
    println(searchView.query.toString())
    getSearchVolunteerInformation(searchView.query.toString())
    hideKeyboard()
    return true
  }

  // 쿼리가 변할 때마다 반응
  override fun onQueryTextChange(p0: String?): Boolean {
    if (p0 == "") {
      searchList.clear()
      getSearchInformation()
    }
    return true
  }

  // 키보드 숨기기
  private fun hideKeyboard() {
    val hide = activity?.act?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
    hide!!.hideSoftInputFromWindow(searchView.windowToken, 0)
  }

}