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
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelUser


class UserAdapter(
    var context: Context,
    var arrOfUser: ArrayList<ModelUser>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<UserAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_user, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfUser.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvUserName.text = arrOfUser[position].userName
        viewHolder.tvMobileNo.text = "Mobile: "+arrOfUser[position].userMobile

        viewHolder.ivEdit.tag = position
        viewHolder.ivDelete.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvMobileNo: TextView = view.findViewById(R.id.tvMobileNo)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)

        init {
            ivEdit.setOnClickListener(this)
            ivDelete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.ivEdit || v.id == R.id.ivDelete) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v,position)
            }
        }

    }
}