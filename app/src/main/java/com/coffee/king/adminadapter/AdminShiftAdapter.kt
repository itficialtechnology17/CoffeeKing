package com.coffee.king.adminadapter

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
import com.coffee.king.model.ModelShift


class AdminShiftAdapter(
    var context: Context,
    var arrOfShift: ArrayList<ModelShift>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<AdminShiftAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_admin_shift, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfShift.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvShiftName.text = arrOfShift[position].shiftName
        viewHolder.tvStartTime.text = arrOfShift[position].shiftStartTime
        viewHolder.tvEndTime.text = arrOfShift[position].shiftEndTime

        viewHolder.ivEdit.tag = position
        viewHolder.ivDelete.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val rlShift: RelativeLayout = view.findViewById(R.id.rlShift)
        val tvShiftName: TextView = view.findViewById(R.id.tvShiftName)
        val tvStartTime: TextView = view.findViewById(R.id.tvStartTime)
        val tvEndTime: TextView = view.findViewById(R.id.tvEndTime)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)


        init {
            ivEdit.setOnClickListener(this)
            ivDelete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.ivEdit || v.id==R.id.ivDelete) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }
    }
}