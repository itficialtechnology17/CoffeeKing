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
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelShift


class ShiftAdapter(
    var context: Context,
    var arrOfShift: ArrayList<ModelShift>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<ShiftAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_shift, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfShift.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvUserName.text = arrOfShift[position].shiftName
        if (arrOfShift[position].shiftStartTime == "") {
            viewHolder.tvUserType.text =""
        } else {
            viewHolder.tvUserType.text =
                "( " + arrOfShift[position].shiftStartTime + " To " + arrOfShift[position].shiftEndTime + " )"
        }

        if (arrOfShift[position].isSelected) {
            viewHolder.ivSelector.setImageResource(R.drawable.ic_selector)
        } else {
            viewHolder.ivSelector.setImageResource(R.drawable.ic_default_selection)
        }

        viewHolder.rlShift.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val rlShift: RelativeLayout = view.findViewById(R.id.rlShift)
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvUserType: TextView = view.findViewById(R.id.tvUserType)
        val ivSelector: ImageView = view.findViewById(R.id.ivSelector)


        init {
            rlShift.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlShift) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }
    }
}