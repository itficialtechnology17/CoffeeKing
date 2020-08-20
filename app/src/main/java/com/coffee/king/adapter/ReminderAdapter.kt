package com.coffee.king.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.model.ModelReminder
import com.coffee.king.utils.Utils
import java.util.*


class ReminderAdapter(
    var context: Context,
    var arrOfReminder: ArrayList<ModelReminder>,
    var arrOfColor: IntArray,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<ReminderAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_reminder, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfReminder.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {


        viewHolder.tvDate.text = Utils.getDay(arrOfReminder[position].reminderDate)
        viewHolder.tvMonth.text = Utils.getMonthName(arrOfReminder[position].reminderDate)
        viewHolder.tvNotes.text = arrOfReminder[position].reminderNotes
        viewHolder.tvResponsible.text = arrOfReminder[position].responsibleName
        viewHolder.tvTime.text = arrOfReminder[position].reminderTime

        val random = Random()
        val randNumber = random.nextInt( arrOfColor.size-0)
        viewHolder.rlContentBG.setBackgroundColor(arrOfColor[randNumber])

        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvMonth: TextView = view.findViewById(R.id.tvMonth)
        val tvNotes: TextView = view.findViewById(R.id.tvNotes)
        val tvResponsible: TextView = view.findViewById(R.id.tvResponsible)
        val tvTime: TextView = view.findViewById(R.id.tvTime)


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