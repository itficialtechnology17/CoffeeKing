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
import com.coffee.king.model.ModelCafeLocation
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelUser


class CafeLocationAdapter(
    var context: Context,
    var arrOfCafeLocation: ArrayList<ModelCafeLocation>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<CafeLocationAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_cafe_location, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfCafeLocation.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvCafeName.text = arrOfCafeLocation[position].cafeLocation
        viewHolder.rlCafeContentBG.tag=position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val rlCafeContentBG: RelativeLayout = view.findViewById(R.id.rlCafeContentBG)
        val tvCafeName: TextView = view.findViewById(R.id.tvCafeName)

        init {
            rlCafeContentBG.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlCafeContentBG) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v,position)
            }
        }

    }
}