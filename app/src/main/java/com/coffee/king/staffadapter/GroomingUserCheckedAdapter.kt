package com.coffee.king.staffadapter

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
import com.coffee.king.model.ModelEventType
import com.coffee.king.model.ModelUser


class GroomingUserCheckedAdapter(
    var context: Context,
    var arrOfUser: ArrayList<ModelUser>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<GroomingUserCheckedAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_grooming_user, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfUser.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvEventName.text = arrOfUser[position].userName

        viewHolder.tvIndex.text=""+(position+1)+"."
        if(arrOfUser[position].isSelected){
            viewHolder.tvChecked.text="Checked"
            viewHolder.tvChecked.setTextColor(context.resources.getColor(R.color.colorGreen))
        }else{
            viewHolder.tvChecked.text="Remain"
            viewHolder.tvChecked.setTextColor(context.resources.getColor(R.color.colorBlack))
        }

        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvEventName: TextView = view.findViewById(R.id.tvEventName)
        val tvChecked: TextView = view.findViewById(R.id.tvChecked)
        val tvIndex: TextView = view.findViewById(R.id.tvIndex)

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