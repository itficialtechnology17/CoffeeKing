package com.coffee.king.fragments


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.coffee.king.R
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import kotlinx.android.synthetic.main.fragment_summary.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SummaryFragment(var modelEventByDate: ModelEventByDate) : Fragment() {

    lateinit var viewFragment: View
    var discountAmount = "0"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_summary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewFragment=view
        initDefine()
        initAction()
    }

    private fun initAction() {

        viewFragment.rgFStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbEnquiry -> modelEventByDate.status = "1"
                R.id.rbConfirm -> modelEventByDate.status = "2"
                R.id.rbDone -> modelEventByDate.status = "3"
                R.id.rbCancelled -> modelEventByDate.status = "4"
            }
        }

        viewFragment.btnUpdate.setOnClickListener {
            discountAmount = viewFragment.etDiscount.text.toString()
            modelEventByDate.discountAmount = discountAmount
            viewFragment.etDiscount.clearFocus()
            updateStatusDiscount()
        }
    }

    private fun initDefine() {

        viewFragment.tvAdvancePaid.text = "\u20A8 " + modelEventByDate.advancePaidAmount
        viewFragment.tvTotalAmount.text = "\u20A8 " + modelEventByDate.totalAmount

        when {
            modelEventByDate.status == "1" -> viewFragment.rbEnquiry.isChecked = true
            modelEventByDate.status == "2" -> viewFragment.rbConfirm.isChecked = true
            modelEventByDate.status == "3" -> viewFragment.rbDone.isChecked = true
            modelEventByDate.status == "4" -> viewFragment.rbCancelled.isChecked = true
        }
    }

    private fun updateStatusDiscount() {

        viewFragment.rlUpdateLoader.visibility = View.VISIBLE
        viewFragment.btnUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.updateStatusDiscount(
            modelEventByDate.bookingId,
            modelEventByDate.status,
            discountAmount
        )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                viewFragment.rlUpdateLoader.visibility = View.GONE
                viewFragment.btnUpdate.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                viewFragment.rlUpdateLoader.visibility = View.GONE
                viewFragment.btnUpdate.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

}
