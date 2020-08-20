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
import com.coffee.king.model.ModelCategory


class FoodCategoryAdapter(
    var context: Context,
    var arrOfCategory: ArrayList<ModelCategory>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<FoodCategoryAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_food_category, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfCategory.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvCategoryName.text = arrOfCategory[position].categoryName

        if(arrOfCategory[position].isSelected){
            viewHolder.ivSelector.visibility=View.VISIBLE
        }else{
            viewHolder.ivSelector.visibility=View.INVISIBLE
        }

        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val ivSelector: ImageView = view.findViewById(R.id.ivSelector)

        init {
            rlContentBG.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlContentBG) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}