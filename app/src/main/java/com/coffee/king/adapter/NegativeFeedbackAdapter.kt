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
import com.coffee.king.model.ModelReminder
import com.coffee.king.utils.Utils
import java.util.*


class NegativeFeedbackAdapter(
    var context: Context,
    var arrOfNegativeFeedback: ArrayList<ModelNegativeFeedback>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<NegativeFeedbackAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_negative_feedback, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfNegativeFeedback.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {
        viewHolder.tvDate.text = arrOfNegativeFeedback[position].addedDate
        viewHolder.tvIssue.text = arrOfNegativeFeedback[position].issue
        viewHolder.tvResponsible.text = "Responsible: "+arrOfNegativeFeedback[position].responsibleName
        viewHolder.tvSolution.text = arrOfNegativeFeedback[position].solution
        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvIssue: TextView = view.findViewById(R.id.tvIssue)
        val tvSolution: TextView = view.findViewById(R.id.tvSolution)
        val tvResponsible: TextView = view.findViewById(R.id.tvResponsible)


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