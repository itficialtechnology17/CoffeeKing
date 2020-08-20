package com.coffee.king.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.coffee.king.R
import com.coffee.king.manager.ManagerActivity
import com.coffee.king.model.ModelBookEvent
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.responseCallback.BookEventResponse
import com.coffee.king.responseCallback.DashboardResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.kotharirefrigeration.constant.ConstantKey
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(), OnDateSelectedListener {


    lateinit var context: Context
    var arrOfModelBookEvent = ArrayList<ModelBookEvent>()

    var arrOfAdvancePayment = ArrayList<ModelEventByDate>()
    var arrOfUpcomingEvent = ArrayList<ModelEventByDate>()
//    var arrOfPastEvent = ArrayList<ModelEventByDate>()
    var arrOfEnquiryEvent = ArrayList<ModelEventByDate>()

    var todayDate = ""
    var userType=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        context = this
        todayDate = Utils.getCurrentDate()
        initDefine()
        initAction()

        getBookingEvent()
        getDashboard()
        setLogout()
    }

    private fun setLogout() {
//        userType = Utils.getPref(context, ConstantKey.KEY_USER_TYPE, "").toString()
//        if (userType == "Admin" || userType=="Manager") {
//            ivSetting.setImageResource(R.drawable.ic_settings)
//        } else {
//            ivSetting.setImageResource(R.drawable.ic_logout)
//        }
    }

    private fun initDefine() {
        calendarView.setOnDateChangedListener(this)


//        FirebaseApp.initializeApp(this)
//        FirebaseInstanceId.getInstance().instanceId
//            .addOnCompleteListener {
//                if (it.isSuccessful) {
//                    val token = it.result?.token
//                    Log.e("<> Token: ", token)
//                    if (token != null) {
//                        Utils.setPref(context, ConstantKey.TOKEN_ID, token)
//                    }
//                }
//            }.addOnFailureListener {
//                it.message
//            }
    }

    override fun onResume() {
        super.onResume()
        getBookingEvent()
    }

    private fun initAction() {

        llAdvancePayment.setOnClickListener {
            if (arrOfAdvancePayment.isNotEmpty()) {
                startActivity(
                    Intent(this, BookEventListActivity::class.java)
                        .putExtra(ConstantKey.KEY_ARRAY, arrOfAdvancePayment)
                        .putExtra(ConstantKey.KEY_STRING, Utils.getCurrentDateAsDB())
                )
            }
        }

        llEnquiryEvent.setOnClickListener {
            if (arrOfEnquiryEvent.isNotEmpty()) {
                startActivity(
                    Intent(this, EnquiryListActivity::class.java)
                        .putExtra(ConstantKey.KEY_ARRAY, arrOfEnquiryEvent)
                        .putExtra(ConstantKey.KEY_STRING, Utils.getCurrentDateAsDB())
                )
            }
        }


        llUpcomingEvent.setOnClickListener {
            if (arrOfUpcomingEvent.isNotEmpty()) {
                startActivity(
                    Intent(this, BookEventListActivity::class.java)
                        .putExtra(ConstantKey.KEY_ARRAY, arrOfUpcomingEvent)
                        .putExtra(ConstantKey.KEY_STRING, Utils.getCurrentDateAsDB())
                )
            }
        }


        ivSetting.setOnClickListener {
//            if (userType == "Admin" || userType=="Manager") {
//
//            }else{
//                confirmLogout()
//            }
            startActivity(Intent(context, SettingActivity::class.java))
        }

        btnManagerTask.setOnClickListener {
            startActivity(Intent(context, ManagerActivity::class.java))
        }
    }

    private fun confirmLogout(){
        AlertDialog.Builder(this)
            .setMessage("Confirm Logout ?")
            .setNegativeButton(android.R.string.no) { dialog, i ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                Utils.setPref(context, ConstantKey.KEY_IS_LOGIN, false)
                startActivity(
                    Intent(
                        context,
                        LoginActivity::class.java
                    ).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
                finish()

            }
            .create()
            .show()
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        val year = date.year.toString()
        var month = date.month.toString()
        var day = date.day.toString()
        if (month.length == 1) {
            month = "0$month"
        }
        if (day.length == 1) {
            day = "0$day"
        }
        val selectedDate = "$year-$month-$day"

        val arrOfBookEventByDate = getArrayOfEventByDate(selectedDate)

        if (!selected) {
            calendarView.setDateSelected(date, true)
            startActivity(
                Intent(this, BookEventListActivity::class.java)
                    .putExtra(ConstantKey.KEY_ARRAY, arrOfBookEventByDate)
                    .putExtra(ConstantKey.KEY_STRING, selectedDate)
            )
        } else {
            calendarView.setDateSelected(date, false)

            startActivity(
                Intent(this, BookEventListActivity::class.java)
                    .putExtra(ConstantKey.KEY_ARRAY, arrOfBookEventByDate)
                    .putExtra(ConstantKey.KEY_STRING, selectedDate)
            )
        }
    }

    private fun getArrayOfEventByDate(selectedDate: String): ArrayList<ModelEventByDate> {
        var arrOfBookEventByDate = ArrayList<ModelEventByDate>()
        for (i in 0 until arrOfModelBookEvent.size) {
            if (arrOfModelBookEvent[i].dateOfEvent == selectedDate) {
                arrOfBookEventByDate = arrOfModelBookEvent[i].arrOfEventByDate
            }
        }
        return arrOfBookEventByDate
    }


    private fun getBookingEvent() {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getBookingEvent()

        call.enqueue(object : Callback<BookEventResponse> {
            override fun onResponse(
                call: Call<BookEventResponse>,
                response: Response<BookEventResponse>
            ) {
                if (response.body() != null) {
                    val bookEventResponse = response.body() as BookEventResponse
                    arrOfModelBookEvent = bookEventResponse.arrOfBookEvent
                    setSelectedDate()
                }
            }

            override fun onFailure(call: Call<BookEventResponse>, t: Throwable) {

                t.printStackTrace()
            }
        })
    }

    private fun getDashboard() {


        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getDashboard(todayDate)

        call.enqueue(object : Callback<DashboardResponse> {
            override fun onResponse(
                call: Call<DashboardResponse>,
                response: Response<DashboardResponse>
            ) {
                if (response.body() != null) {
                    val dashboardResponse = response.body() as DashboardResponse
                    arrOfAdvancePayment = dashboardResponse.arrOfAdvancePayment
                    arrOfUpcomingEvent = dashboardResponse.arrOfUpcomingEvent
                    arrOfEnquiryEvent = dashboardResponse.arrOfEnquiry
                    setDashboardData()
                }
            }

            override fun onFailure(call: Call<DashboardResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setDashboardData() {
        var advancePayment = 0
        for (i in 0 until arrOfAdvancePayment.size) {
            advancePayment += arrOfAdvancePayment[i].advancePaidAmount.toInt()
        }
        tvAdvancePaid.text = "" + advancePayment

        tvUpcomingEvent.text = "" + arrOfUpcomingEvent.size
        tvPastEvent.text = "" + arrOfEnquiryEvent.size

    }

    private fun setSelectedDate() {
        for (i in 0 until arrOfModelBookEvent.size) {
            val modelBookEvent = arrOfModelBookEvent[i]
            val dateOfEvent = modelBookEvent.dateOfEvent
            val separatedDate = dateOfEvent.split("-")
            calendarView.setDateSelected(
                CalendarDay.from(
                    separatedDate[0].toInt(),
                    separatedDate[1].toInt(),
                    separatedDate[2].toInt()
                ), true
            )
        }
    }
}
