package com.coffee.king.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.*
import java.util.*


class DamageDestroyAdapter(
    var context: Context,
    var arrOfDamageDestroy: ArrayList<ModelDamageDestroy>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<DamageDestroyAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_damage_destroy, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfDamageDestroy.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.tvItemName.text = arrOfDamageDestroy[position].itemName
        viewHolder.tvName.text = arrOfDamageDestroy[position].userName
        viewHolder.tvMobileNo.text = arrOfDamageDestroy[position].contactNumber
        viewHolder.tvTimeStamp.text = arrOfDamageDestroy[position].timestamp
        viewHolder.tvReason.text = arrOfDamageDestroy[position].reason

        viewHolder.rlContentBG.tag = position
        viewHolder.tvMobileNo.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val tvReason: TextView = view.findViewById(R.id.tvReason)
        val tvName: TextView = view.findViewById(R.id.tvName)
        val tvMobileNo: TextView = view.findViewById(R.id.tvMobileNo)
        val tvTimeStamp: TextView = view.findViewById(R.id.tvTimeStamp)


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