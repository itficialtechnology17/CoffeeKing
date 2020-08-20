package com.coffee.king.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelDate
import com.coffee.king.model.ModelEventType
import com.prolificinteractive.materialcalendarview.CalendarDay


class SelectedDateAdapter(
    var context: Context,
    var arrOfDate: ArrayList<ModelDate>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<SelectedDateAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_selected_date, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfDate.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvDateDay.text = arrOfDate[position].dateDay

        if(arrOfDate[position].isSelected){
            viewHolder.rlSelectedDate.setBackgroundResource(R.drawable.ic_circle_bg_theme)
        }else{
            viewHolder.rlSelectedDate.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
        }

        viewHolder.rlSelectedDate.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlSelectedDate: RelativeLayout = view.findViewById(R.id.rlSelectedDate)
        val tvDateDay: TextView = view.findViewById(R.id.tvDateDay)


        init {
            rlSelectedDate.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlSelectedDate) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}