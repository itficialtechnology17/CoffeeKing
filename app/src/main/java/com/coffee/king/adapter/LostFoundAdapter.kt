package com.coffee.king.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.*
import com.coffee.king.utils.Utils
import java.util.*


class LostFoundAdapter(
    var context: Context,
    var arrOfLostFound: ArrayList<ModelLostFound>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<LostFoundAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_lost_found, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfLostFound.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.tvItemName.text = arrOfLostFound[position].itemName
        viewHolder.tvName.text = arrOfLostFound[position].name
        viewHolder.tvDate.text = arrOfLostFound[position].dateOfLost
        viewHolder.tvTime.text = arrOfLostFound[position].timeOfOfLost
        viewHolder.tvMobileNo.text = arrOfLostFound[position].contactNumber
        viewHolder.tvTimeStamp.text = arrOfLostFound[position].foundTimeStamp
        viewHolder.tvStatus.text = arrOfLostFound[position].status

        if(arrOfLostFound[position].status.toInt()==1){
            viewHolder.tvStatus.text="L"
            viewHolder.tvStatus.setBackgroundResource(R.drawable.ic_bg_circle_lost)
            viewHolder.viewLine.setBackgroundColor(context.resources.getColor(R.color.colorRed))
            viewHolder.llDateTime.visibility=View.VISIBLE
            viewHolder.tvTimeStamp.visibility=View.GONE
        }else if(arrOfLostFound[position].status.toInt()==2){
            viewHolder.tvStatus.text="F"
            viewHolder.tvStatus.setBackgroundResource(R.drawable.ic_bg_circle_found)
            viewHolder.llDateTime.visibility=View.GONE
            viewHolder.tvTimeStamp.visibility=View.VISIBLE
            viewHolder.viewLine.setBackgroundColor(context.resources.getColor(R.color.colorGreen))
        }
        viewHolder.rlContentBG.tag = position
        viewHolder.tvMobileNo.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val llDateTime: LinearLayout = view.findViewById(R.id.llDateTime)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvMobileNo: TextView = view.findViewById(R.id.tvMobileNo)
        val tvTimeStamp: TextView = view.findViewById(R.id.tvTimeStamp)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
        val viewLine: View = view.findViewById(R.id.viewLine)

        init {
            rlContentBG.setOnClickListener(this)
            tvMobileNo.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlContentBG) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }else if(v.id == R.id.tvMobileNo){
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}