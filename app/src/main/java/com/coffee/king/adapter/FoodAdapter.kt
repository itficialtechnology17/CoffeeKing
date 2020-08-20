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
import com.coffee.king.clickcallback.NestedItemClickCallback
import com.coffee.king.model.ModelFood


class FoodAdapter(
    var context: Context,
    var arrOfFood: ArrayList<ModelFood>,
    var parentPosition: Int,
    var nestedItemClickCallback: NestedItemClickCallback,
    var i: Int
) :
    RecyclerView.Adapter<FoodAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_food, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfFood.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvFoodName.text = arrOfFood[position].foodName

        if(i==1){
            viewHolder.ivSelector.visibility=View.VISIBLE
        }else{
            viewHolder.ivSelector.visibility=View.INVISIBLE
        }
        if(arrOfFood[position].isSelected){
            viewHolder.ivSelector.setImageResource(R.drawable.ic_selector)
        }else{
            viewHolder.ivSelector.setImageResource(R.drawable.ic_default_selection)
        }

        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvFoodName: TextView = view.findViewById(R.id.tvFoodName)
        val ivSelector: ImageView = view.findViewById(R.id.ivSelector)

        init {
            rlContentBG.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlContentBG) {
                val position = v.tag as Int
                nestedItemClickCallback.onNestedItemClick(v, parentPosition,position)
            }
        }

    }
}