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
import com.coffee.king.model.ModelShiftDate
import com.prolificinteractive.materialcalendarview.CalendarDay


class ShiftDateAdapter(
    var context: Context,
    var arrOfShiftDate: ArrayList<ModelShiftDate>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<ShiftDateAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_shift_date, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfShiftDate.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvDate.text = arrOfShiftDate[position].shiftDate
        if (arrOfShiftDate[position].shiftName == "Off"){
            viewHolder.tvShiftType.text="Off"
        }else{
            viewHolder.tvShiftType.text = arrOfShiftDate[position].shiftName+
                    " ( " + arrOfShiftDate[position].shiftStartTime + " To " + arrOfShiftDate[position].shiftEndTime + " ) "
        }


        if(arrOfShiftDate[position].isSelected){
            viewHolder.tvDate.setTextColor(context.resources.getColor(R.color.colorTheme))
        }else{
            viewHolder.tvDate.setTextColor(context.resources.getColor(R.color.colorGreen))
        }

        viewHolder.rlSelectedShiftDate.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlSelectedShiftDate: RelativeLayout = view.findViewById(R.id.rlSelectedShiftDate)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvShiftType: TextView = view.findViewById(R.id.tvShiftType)


        init {
            rlSelectedShiftDate.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlSelectedDate) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}