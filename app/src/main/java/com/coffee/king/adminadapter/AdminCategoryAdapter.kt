package com.coffee.king.adminadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCategory


class AdminCategoryAdapter(
    var context: Context,
    var arrOfCategory: ArrayList<ModelCategory>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<AdminCategoryAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_admin_category, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfCategory.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvCategoryName.text = arrOfCategory[position].categoryName
        viewHolder.tvPrice.text="Price : "+arrOfCategory[position].categoryPrice
        viewHolder.tvMaximum.text="Maximum Select : "+arrOfCategory[position].maximum

        viewHolder.ivDelete.tag = position
        viewHolder.ivEdit.tag = position

    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val tvMaximum: TextView = view.findViewById(R.id.tvMaximum)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)

        init {
            ivDelete.setOnClickListener(this)
            ivEdit.setOnClickListener(this)
        }


        override fun onClick(v: View) {
            if(v.id==R.id.ivDelete || v.id==R.id.ivEdit){
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}