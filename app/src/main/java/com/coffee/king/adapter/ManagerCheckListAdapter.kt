package com.coffee.king.adapter

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCheckList


class ManagerCheckListAdapter(
    var context: Context,
    var arrOfCheckList: ArrayList<ModelCheckList>,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<ManagerCheckListAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_manager_check_list, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfCheckList.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvCheckListTitle.text = arrOfCheckList[position].checkedListTitle
        viewHolder.cbTick.isChecked = arrOfCheckList[position].isChecked=="1"

        viewHolder.etReason.setText(arrOfCheckList[position].reason)

        viewHolder.etReason.addTextChangedListener(object :TextWatcher{
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                arrOfCheckList[position].reason=s.toString()
            }

        })

        viewHolder.cbTick.tag = position
        viewHolder.etReason.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener{


        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvCheckListTitle: TextView = view.findViewById(R.id.tvCheckListTitle)
        val cbTick: CheckBox = view.findViewById(R.id.cbTick)
        val etReason: EditText = view.findViewById(R.id.etReason)

        init {
            cbTick.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (v.id == R.id.cbTick) {
                val position = v.tag as Int
                itemClickCallback.onItemClick(v,position)
            }
        }

    }
}