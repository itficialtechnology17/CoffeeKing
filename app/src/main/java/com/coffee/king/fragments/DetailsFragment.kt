package com.coffee.king.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.coffee.king.R
import com.coffee.king.model.ModelEventByDate
import kotlinx.android.synthetic.main.fragment_details.view.*


class DetailsFragment(var modelEventByDate: ModelEventByDate) : Fragment() {

    lateinit var viewFragment:View
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFragment=view
        initDefine()
    }

    private fun initDefine() {
        viewFragment.tvCustomerName.text = modelEventByDate.customerName
        viewFragment.tvCustomerMobile.text = modelEventByDate.customerNumber

        viewFragment.tvCustomerEmail.text = modelEventByDate.customerEmail
        viewFragment.tvNoOfPeople.text = modelEventByDate.noOfPeople
        viewFragment.tvDateOfEvent.text = modelEventByDate.dateOfEvent
        viewFragment.tvTime.text = modelEventByDate.startTime
        viewFragment.tvEventType.text = modelEventByDate.eventTitle

        if (modelEventByDate.bookingType == "1") {
            viewFragment.tvBookingType.text = "Event"
        } else {
            viewFragment.tvBookingType.text = "Couple"
        }

        if (modelEventByDate.bookingType == "1") {
            viewFragment.tvBookingVenue.text = "Hall"
        } else {
            viewFragment.tvBookingVenue.text = "Table"
        }

        if (modelEventByDate.bookingPackageType == "1") {
            viewFragment.tvPackageType.text = "A La Carte"
        } else {
            viewFragment.tvPackageType.text = "Package"
        }
    }

}
