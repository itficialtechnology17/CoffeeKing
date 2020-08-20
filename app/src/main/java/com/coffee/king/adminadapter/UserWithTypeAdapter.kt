package com.coffee.king.adminadapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelUser


class UserWithTypeAdapter(
    var context: Context,
    var arrOfUser: ArrayList<ModelUser>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<UserWithTypeAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_user_type, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfUser.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvUserName.text = arrOfUser[position].userName
        viewHolder.tvUserType.text = "("+arrOfUser[position].userTypeName+")"

        if(arrOfUser[position].isSelected){
            viewHolder.ivSelector.setImageResource(R.drawable.ic_selector)
        }else{
            viewHolder.ivSelector.setImageResource(R.drawable.ic_default_selection)
        }

        viewHolder.rlBG.tag=position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val rlBG: RelativeLayout = view.findViewById(R.id.rlBG)
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvUserType: TextView = view.findViewById(R.id.tvUserType)
        val ivSelector: ImageView = view.findViewById(R.id.ivSelector)


        init {
            rlBG.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.rlBG) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v,position)
            }
        }

    }
}