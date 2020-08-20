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
import java.util.*


class BookEventAdapter(
    var context: Context,
    var arrOfBookEventByDate: ArrayList<ModelEventByDate>,
    var arrOfColor: IntArray,
    var itemClickCallback: ItemClickCallback
) :
    RecyclerView.Adapter<BookEventAdapter.CustomViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {

        val viewCart = LayoutInflater.from(context).inflate(R.layout.cell_of_book_event, null)
        return CustomViewHolder(viewCart)
    }

    override fun getItemCount(): Int {
        return arrOfBookEventByDate.size
    }

    override fun onBindViewHolder(viewHolder: CustomViewHolder, position: Int) {

        viewHolder.tvCustomerName.text =arrOfBookEventByDate[position].customerName
        viewHolder.tvNumber.text =  arrOfBookEventByDate[position].customerNumber
        viewHolder.tvDateOfEvent.text = "Date: "+arrOfBookEventByDate[position].dateOfEvent
        viewHolder.tvTime.text = "Time: " +arrOfBookEventByDate[position].startTime
        if(arrOfBookEventByDate[position].bookingType=="1"){
            viewHolder.tvVenue.text = "Venue: Hall"
        }else{
            viewHolder.tvVenue.text = "Venue: Table"
        }

        if(arrOfBookEventByDate[position].bookingPackageType=="1"){
            viewHolder.tvPackageType.text = "Type: A La Carte"
        }else{
            viewHolder.tvPackageType.text = "Type: Package"
        }
        viewHolder.tvPeople.text = "No. Of People: " +arrOfBookEventByDate[position].noOfPeople
        if(arrOfBookEventByDate[position].status=="1"){
            viewHolder.tvStatus.text = "Status: Enquiry"

        }else if(arrOfBookEventByDate[position].status=="2"){
            viewHolder.tvStatus.text = "Status: Confirm"
        }else if(arrOfBookEventByDate[position].status=="3"){
            viewHolder.tvStatus.text = "Status: Complete"
        }else if(arrOfBookEventByDate[position].status=="4"){
            viewHolder.tvStatus.text = "Status: Cancel"
        }



        val random = Random()
        val randNumber = random.nextInt( arrOfColor.size-0)
        viewHolder.rlContentBG.setBackgroundColor(arrOfColor[randNumber])

        viewHolder.rlContentBG.tag = position
    }

    inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val rlContentBG: RelativeLayout = view.findViewById(R.id.rlContentBG)
        val tvCustomerName: TextView = view.findViewById(R.id.tvCustomerName)
        val tvDateOfEvent: TextView = view.findViewById(R.id.tvDateOfEvent)
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvVenue: TextView = view.findViewById(R.id.tvVenue)
        val tvPackageType: TextView = view.findViewById(R.id.tvPackageType)
        val tvPeople: TextView = view.findViewById(R.id.tvPeople)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)

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