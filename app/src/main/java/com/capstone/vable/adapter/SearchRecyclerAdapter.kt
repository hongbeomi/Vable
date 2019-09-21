package com.capstone.vable.adapter

import android.animation.ValueAnimator
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.capstone.vable.R
import com.capstone.vable.data.SearchItem
import kotlinx.android.synthetic.main.search_item.view.*
import java.util.ArrayList

class SearchRecyclerAdapter(private val searchList: ArrayList<SearchItem>) :
  RecyclerView.Adapter<SearchRecyclerAdapter.ViewHolder>(), View.OnTouchListener {

  val selectedItems = SparseBooleanArray()
  var prePosition = -1

  //아이템의 갯수
  override fun getItemCount(): Int {
    return searchList.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context)
      .inflate(R.layout.search_item, parent, false)
    return ViewHolder(v)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.itemView.searchDescriptionTextView.apply {
      setOnTouchListener(this@SearchRecyclerAdapter)
      movementMethod = ScrollingMovementMethod.getInstance()
    }
    holder.bindItems(searchList[position])
  }


  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val cardView = itemView.findViewById<CardView>(R.id.searchCardView)
    private val descriptionLayout =
      itemView.findViewById<LinearLayout>(R.id.searchDescriptionLayout)

    override fun onClick(v: View?) {

      cardView.isFocusableInTouchMode = true
      cardView.requestFocus()
      if (v!!.id == R.id.searchCardView) run {
        if (selectedItems.get(adapterPosition)) {
          selectedItems.delete(adapterPosition)
        } else {
          selectedItems.delete(prePosition)
          selectedItems.put(adapterPosition, true)
        }
        if (prePosition != -1) notifyItemChanged(prePosition)
        notifyItemChanged(adapterPosition)
        prePosition = adapterPosition
      }
    }

    private fun changeVisibility(isExpanded: Boolean) {
      val dpValue = 300
      val d = itemView.context.resources.displayMetrics.density
      val height = (dpValue * d).toInt()
      val valueAnimator = if (isExpanded) ValueAnimator.ofInt(0, height)
      else ValueAnimator.ofInt(height, 0)
      valueAnimator.duration = 600
      valueAnimator.addUpdateListener { animation ->
        val value = animation.animatedValue as Int
        descriptionLayout.apply {
          layoutParams.height = value
          requestLayout()
          visibility = if (isExpanded) View.VISIBLE else View.GONE
        }
      }

      valueAnimator.start()
    }

    fun bindItems(data: SearchItem) {
      itemView.apply {
        searchTitleTextView.text = data.searchTitle
        searchGenderTextView.text = data.searchGender
        searchLocalTextView.text = data.searchLocal
        searchSubLocalTextView.text = data.searchSubLocal
        searchDescriptionTextView.text = data.searchDescription
      }
      changeVisibility(selectedItems.get(adapterPosition))
      cardView.setOnClickListener(this)
    }
  }

  override fun onTouch(v: View?, event: MotionEvent?): Boolean {
    v?.parent?.parent?.requestDisallowInterceptTouchEvent(true)
    return false
  }

}