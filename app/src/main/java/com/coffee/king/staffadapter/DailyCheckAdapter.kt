package com.coffee.king.staffadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.*
import java.util.*


class DailyCheckAdapter(
    var context: Context,
    var arrOfCheckList: ArrayList<ModelDailyCheckList>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<DailyCheckAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_duty, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfCheckList.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        if(arrOfCheckList[position].checkListType=="1") {
            viewHolder.tvDutyTitle.text = arrOfCheckList[position].titleTime
        }else if(arrOfCheckList[position].checkListType=="2"){
            viewHolder.tvDutyTitle.text = arrOfCheckList[position].titleText
        }else if(arrOfCheckList[position].checkListType=="3"){
            viewHolder.tvDutyTitle.text = arrOfCheckList[position].titleText
        }

        viewHolder.cbDuty.isChecked=arrOfCheckList[position].isSelected

        viewHolder.rltBGDuty.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rltBGDuty: RelativeLayout = view.findViewById(R.id.rltBGDuty)
        val cbDuty: CheckBox = view.findViewById(R.id.cbDuty)
        val tvDutyTitle: TextView = view.findViewById(R.id.tvDutyTitle)


        init {
            rltBGDuty.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rltBGDuty) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}