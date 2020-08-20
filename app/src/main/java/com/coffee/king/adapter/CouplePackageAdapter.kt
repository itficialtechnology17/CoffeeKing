package com.coffee.king.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCouplePackage


class CouplePackageAdapter(
    var context: Context,
    var arrOfCouplePackage: ArrayList<ModelCouplePackage>,
    var itemClickCallback: ItemClickCallback,
    var isAddVisible: Int
) :
    RecyclerView.Adapter<CouplePackageAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_couple_package, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfCouplePackage.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvEventName.text = arrOfCouplePackage[position].packageTitle
        viewHolder.tvPrice.text = "Price: " + arrOfCouplePackage[position].packagePrice

        if (isAddVisible != 0) {
            if (arrOfCouplePackage[position].isSelected) {
                viewHolder.tvAddRemove.text = "Remove"
                viewHolder.tvAddRemove.setBackgroundResource(R.drawable.ic_bg_btn_light)
            } else {
                viewHolder.tvAddRemove.text = "Add"
                viewHolder.tvAddRemove.setBackgroundResource(R.drawable.ic_bg_btn)
            }
        }else{
            viewHolder.tvAddRemove.visibility=View.GONE
        }

        viewHolder.tvAddRemove.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val tvEventName: TextView = view.findViewById(R.id.tvEventName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvAddRemove: TextView = view.findViewById(R.id.tvAddRemove)

        init {
            tvAddRemove.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.tvAddRemove) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}