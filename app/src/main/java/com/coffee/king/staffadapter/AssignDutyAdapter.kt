package com.coffee.king.staffadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.*
import java.util.*


class AssignDutyAdapter(
    var context: Context,
    var arrOfAssignDuty: ArrayList<ModelAssignDuty>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<AssignDutyAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_assign_duty, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfAssignDuty.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.tvDutyTitle.text = arrOfAssignDuty[position].assignDutyText
        if(arrOfAssignDuty[position].assignDutyType=="0"){
            viewHolder.ivNext.visibility=View.GONE
        }else{
            viewHolder.ivNext.visibility=View.VISIBLE
        }
        viewHolder.rlCellBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlCellBG: RelativeLayout = view.findViewById(R.id.rlCellBG)
        val tvDutyTitle: TextView = view.findViewById(R.id.tvDutyTitle)
        val ivNext: ImageView = view.findViewById(R.id.ivNext)


        init {
            rlCellBG.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlCellBG) {
                val position = v.tag as Int
                if(arrOfAssignDuty[position].assignDutyType!="0") {
                    itemClickCallback.onItemClick(v, position)
                }
            }
        }

    }
}