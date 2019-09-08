package com.capstone.vable.adapter

import android.animation.ValueAnimator
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.text.method.ScrollingMovementMethod
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.capstone.vable.R
import com.capstone.vable.data.RecommendItem
import kotlinx.android.synthetic.main.recommend_item.view.*
import java.util.ArrayList

class RecommendRecyclerAdapter(private val recommendList: ArrayList<RecommendItem>) :
  RecyclerView.Adapter<RecommendRecyclerAdapter.ViewHolder>() , View.OnTouchListener {

  val selectedItems = SparseBooleanArray()
  var prePosition = -1

  //아이템의 갯수
  override fun getItemCount(): Int {
    return recommendList.size
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val v = LayoutInflater.from(parent.context).inflate(R.layout.recommend_item,
      parent, false)
    return ViewHolder(v)
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    holder.itemView.recommendDescriptionTextView.apply {
      setOnTouchListener(this@RecommendRecyclerAdapter)
      movementMethod = ScrollingMovementMethod.getInstance()
    }
    holder.bindItems(recommendList[position])
  }

  inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

    private val cardView = itemView.findViewById<CardView>(R.id.recommendCardView)
    private val descriptionLayout =
      itemView.findViewById<LinearLayout>(R.id.recommendDescriptionLayout)

    override fun onClick(v: View?) {
      cardView.isFocusableInTouchMode = true
      cardView.requestFocus()
      if (v!!.id == R.id.recommendCardView) run {
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
      val dpValue = 150
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

    fun bindItems(data: RecommendItem) {
      itemView.apply {
        recommendTitleTextView.text = data.recommendTitle
        recommendGenderTextView.text = data.recommendGender
        recommendLocalTextView.text = data.recommendLocal
        recommendSubLocalTextView.text = data.recommendSubLocal
        recommendDescriptionTextView.text = data.recommendDescription
        recommendTypeTextView.text = data.recommendType
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


