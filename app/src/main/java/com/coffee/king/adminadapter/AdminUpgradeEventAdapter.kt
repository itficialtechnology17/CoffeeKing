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
import com.coffee.king.model.ModelUpgradeEvent


class AdminUpgradeEventAdapter(
    var context: Context,
    var arrOfModelUpgradeEvent: ArrayList<ModelUpgradeEvent>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<AdminUpgradeEventAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart =
            LayoutInflater.from(context).inflate(R.layout.cell_of_admin_upgrade_event, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfModelUpgradeEvent.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvEventName.text = arrOfModelUpgradeEvent[position].upgradeEventName
        viewHolder.tvPrice.text = "Price: " + arrOfModelUpgradeEvent[position].upgradeEventPrice

        viewHolder.ivEdit.tag = position
        viewHolder.ivDelete.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {


        val tvEventName: TextView = view.findViewById(R.id.tvEventName)
        val tvPrice: TextView = view.findViewById(R.id.tvPrice)
        val ivEdit: ImageView = view.findViewById(R.id.ivEdit)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)

        init {
            ivEdit.setOnClickListener(this)
            ivDelete.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.ivEdit || v.id == R.id.ivDelete) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v, position)
            }
        }

    }
}