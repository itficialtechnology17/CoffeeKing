package com.coffee.king.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.NestedItemClickCallback
import com.coffee.king.model.ModelPackage


class CategoryAdapter(
    var context: Context,
    var arrOfPackage: ArrayList<ModelPackage>,
    var nestedItemClickCallback: NestedItemClickCallback,
    var i: Int
) :
    RecyclerView.Adapter<CategoryAdapter.CustomViewHolder>(), NestedItemClickCallback {


    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_category, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfPackage.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvCategoryName.text=arrOfPackage[position].categoryName
        viewHolder.tvPrice.text="Price : "+arrOfPackage[position].categoryPrice
        viewHolder.tvMaximum.text="Maximum Select : "+arrOfPackage[position].maximum


        if(arrOfPackage[position].isSelected){
            viewHolder.rvFood.visibility=View.VISIBLE
            viewHolder.ivArrow.setImageResource(R.drawable.ic__arrow_up)
        }else{
            viewHolder.rvFood.visibility=View.GONE
            viewHolder.ivArrow.setImageResource(R.drawable.ic_arrow_down)
        }

        setAdapter(viewHolder,position)

        viewHolder.rlCategory.tag = position
        viewHolder.ivArrow.tag = position

    }

    private fun setAdapter(viewHolder: CustomViewHolder, position: Int) {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewHolder.rvFood.layoutManager=layoutManager
        val foodAdapter = FoodAdapter(context, arrOfPackage[position].arrOfFood,position, this,i)
        viewHolder.rvFood.adapter = foodAdapter
    }

    override fun onNestedItemClick(view: View, parentPosition: Int, childPosition: Int) {
        nestedItemClickCallback.onNestedItemClick(view,parentPosition,childPosition)
    }


    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val rlCategory: RelativeLayout = view.findViewById(R.id.rlCategory)
        val tvCategoryName: TextView = view.findViewById(R.id.tvCategoryName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val rvFood: RecyclerView = view.findViewById(R.id.rvFood)
        val tvMaximum: TextView = view.findViewById(R.id.tvMaximum)
        val ivArrow: ImageView = view.findViewById(R.id.ivArrow)

        init {
            ivArrow.setOnClickListener(this)
            rlCategory.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.ivArrow || v.id==R.id.rlCategory) {
                val position = v.tag as Int
                nestedItemClickCallback.onNestedItemClick(v, position, 0)
            }
        }

    }
}