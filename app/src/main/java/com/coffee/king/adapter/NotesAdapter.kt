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
import com.coffee.king.model.ModelNegativeFeedback
import com.coffee.king.model.ModelNotes
import com.coffee.king.model.ModelReminder
import com.coffee.king.utils.Utils
import java.util.*


class NotesAdapter(
    var context: Context,
    var arrOfNotes: ArrayList<ModelNotes>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<NotesAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_notes, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfNotes.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.tvTitle.text = arrOfNotes[position].noteTitle
        viewHolder.tvDesc.text = arrOfNotes[position].noteDesc

        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvDesc: TextView = view.findViewById(R.id.tvDesc)

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