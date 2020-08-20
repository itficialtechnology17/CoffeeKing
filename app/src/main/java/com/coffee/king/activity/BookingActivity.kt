package com.coffee.king.activity

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.EventTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.model.ModelEventType
import com.coffee.king.responseCallback.EventsResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_booking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class BookingActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    lateinit var eventTypeAdapter: EventTypeAdapter
    var arrOfEventType = ArrayList<ModelEventType>()
    var modelEventByDate = ModelEventByDate()

    lateinit var calendar: Calendar


    var bookingId = ""
    var bookingType = 1
    var eventId = ""
    var customerName = ""
    var customerNumber = ""
    var customerEmail = ""
    var noOfPeople = ""
    var bookingVenue = 1
    var packageType = 1
    var dateOfEnquiry = ""
    var dateOfEvent = ""
    var fromTime = ""
    var toTime = ""
    var isFromTimeSelected = false
    var isToTimeSelected = false
    var referenceName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {

        if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
            modelEventByDate =
                intent.getSerializableExtra(ConstantKey.KEY_ARRAY) as ModelEventByDate

            bookingId = modelEventByDate.bookingId
            bookingType = modelEventByDate.bookingType.toInt()
            eventId = modelEventByDate.eventTypeId
            customerName = modelEventByDate.customerName
            customerNumber = modelEventByDate.customerNumber
            customerEmail = modelEventByDate.customerEmail
            noOfPeople = modelEventByDate.noOfPeople
            packageType = modelEventByDate.bookingPackageType.toInt()
            bookingVenue = modelEventByDate.bookingType.toInt()
            dateOfEnquiry = modelEventByDate.dateOfEnquiry
            dateOfEvent = modelEventByDate.dateOfEvent
            isFromTimeSelected = true
            fromTime = modelEventByDate.startTime
            toTime = modelEventByDate.endTime
            referenceName = modelEventByDate.referenceName

            btnNext.text = "Update"

            if (bookingType == 1) {
                rbEvent.isChecked = true
                rbCouple.isChecked = false
            } else {
                rbEvent.isChecked = false
                rbCouple.isChecked = true
            }
            etCustomerName.setText(customerName)
            etMobileNumber.setText(customerNumber)
            etEmail.setText(customerEmail)
            etNoOfPeople.setText(noOfPeople)
            if (bookingVenue == 1) {
                rbHall.isChecked = true
                rbTable.isChecked = false
            } else {
                rbHall.isChecked = false
                rbTable.isChecked = true
            }
            tvEnquiryDate.text = dateOfEnquiry
            tvEventDate.text = dateOfEvent
            tvFromTime.text = fromTime
            tvToTime.text = toTime
            etReference.setText(modelEventByDate.referenceName)


        } else {
            rbEvent.isChecked = true
            rbCouple.isChecked = false
            bookingVenue = 1
            bookingType = 1
            packageType = 2

            dateOfEnquiry = Utils.getCurrentDate()
            tvEnquiryDate.text = dateOfEnquiry
            tvEventDate.text =
                Utils.getDateFromString(intent.getStringExtra(ConstantKey.KEY_STRING)!!)
        }
        calendar = Calendar.getInstance()
        getEvents()
    }

    private fun setEventTypeId() {
        for (i in 0 until arrOfEventType.size) {
            if (eventId == arrOfEventType[i].eventId) {
                tvEventType.text = arrOfEventType[i].eventName
            }
        }
    }


    private fun initAction() {

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tvEventDate.text = sdf.format(calendar.time)
                dateOfEvent = tvEventDate.text.toString()
            }

        val fromTimeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            tvFromTime.text = SimpleDateFormat("HH:mm a").format(calendar.time)
            fromTime = tvFromTime.text.toString()
            isFromTimeSelected = true
        }

        val toTimeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            tvToTime.text = SimpleDateFormat("HH:mm a").format(calendar.time)
            toTime = tvToTime.text.toString()
            isToTimeSelected = true
        }

        ivBack.setOnClickListener {
            finish()
        }


        rlEventType.setOnClickListener {
            if (arrOfEventType.isEmpty()) {
                getEvents()
            }
            if (progressEvents.visibility != View.VISIBLE) {
                if (viewEventType.visibility == View.GONE) {
                    viewEventType.visibility = View.VISIBLE
                    ivETArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewEventType.visibility = View.GONE
                    ivETArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }
        llDateOfEvent.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        tvFromTime.setOnClickListener {
            TimePickerDialog(
                this,
                fromTimeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        tvToTime.setOnClickListener {
            TimePickerDialog(
                this,
                toTimeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnNext.setOnClickListener {



            customerName = etCustomerName.text.toString()
            customerNumber = etMobileNumber.text.toString()
            customerEmail = etEmail.text.toString()
            noOfPeople = etNoOfPeople.text.toString()
            dateOfEvent = tvEventDate.text.toString()
            fromTime = tvFromTime.text.toString()
            toTime = tvToTime.text.toString()
            referenceName = etReference.text.toString()



            when {
                eventId.isEmpty() -> Toast.makeText(
                    context,
                    "Please Select Event Type",
                    Toast.LENGTH_SHORT
                ).show()
                customerName.isEmpty() -> Toast.makeText(
                    context,
                    "Enter Customer Name",
                    Toast.LENGTH_SHORT
                ).show()
                customerNumber.isEmpty() -> Toast.makeText(
                    context,
                    "Enter Customer Mobile Number",
                    Toast.LENGTH_SHORT
                ).show()
                noOfPeople.isEmpty() -> Toast.makeText(
                    context,
                    "Enter Number Of People",
                    Toast.LENGTH_SHORT
                ).show()
                !isFromTimeSelected -> Toast.makeText(
                    context,
                    "Select Start Time",
                    Toast.LENGTH_SHORT
                ).show()
                else -> if (bookingId.isEmpty()) {
                    addEvent()
                } else {
                    updateEvent()
                }
            }
        }

        rgBookingVenue.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbHall) {
                bookingVenue = 1
            } else if (checkedId == R.id.rbTable) {
                bookingVenue = 2
            }
        }

        rgBookingType.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbEvent) {
                rbHall.isChecked = true
                rbTable.isChecked = false
                bookingVenue = 1
                bookingType = 1
                packageType = 2
            } else if (checkedId == R.id.rbCouple) {
                rbHall.isChecked = false
                rbTable.isChecked = true
                bookingVenue = 2
                bookingType = 2
                packageType = 1
                etNoOfPeople.setText("2")
            }
        }
    }


    private fun setEventAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvEventType.layoutManager = layoutManager
        eventTypeAdapter = EventTypeAdapter(context, arrOfEventType, this)
        rvEventType.adapter = eventTypeAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.rlContentBG) {
            eventId = arrOfEventType[position].eventId
            tvEventType.text = arrOfEventType[position].eventName
            viewEventType.visibility = View.GONE
            ivETArrow.setImageResource(R.drawable.ic_arrow_down)
        }
    }

    private fun getEvents() {

        progressEvents.visibility = View.VISIBLE
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getEvents()

        call.enqueue(object : Callback<EventsResponse> {
            override fun onResponse(
                call: Call<EventsResponse>,
                response: Response<EventsResponse>
            ) {
                progressEvents.visibility = View.GONE
                ivETArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val eventsResponse = response.body() as EventsResponse
                    arrOfEventType = eventsResponse.arrOfModelEventType
                    setEventAdapter()
                    if (eventId.isNotEmpty()) {
                        setEventTypeId()
                    }

                }
            }

            override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                progressEvents.visibility = View.GONE
                ivETArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun addEvent() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE

        val t=Utils.getServerDateFormat(dateOfEnquiry);

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addEvent(
            bookingType,
            userId,
            eventId,
            customerName,
            customerNumber,

            customerEmail,
            noOfPeople,
            bookingVenue,
            packageType,
            dateOfEnquiry,
            dateOfEvent,
            fromTime,
            toTime,
            referenceName
        )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
//                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (responseStatusCode.statusCode == 101) {
                        if (bookingType == 1) {
                            startActivity(
                                Intent(context, PackageActivity::class.java)
                                    .putExtra(ConstantKey.KEY_ID, responseStatusCode.id)
                                    .putExtra(ConstantKey.KEY_STRING, noOfPeople)
                            )
                        } else {
                            startActivity(
                                Intent(context, CouplePackageActivity::class.java)
                                    .putExtra(ConstantKey.KEY_ID, responseStatusCode.id)
                                    .putExtra(ConstantKey.KEY_BOOKING_VENUE, bookingVenue)
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun updateEvent() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.updateEvent(
            bookingId,
            bookingType,
            userId,
            eventId,
            customerName,
            customerNumber,
            customerEmail,
            noOfPeople,
            bookingVenue,
            packageType,
            dateOfEnquiry,
            dateOfEvent,
            fromTime,
            toTime,
            referenceName
        )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (responseStatusCode.statusCode == 101) {

                        modelEventByDate.bookingType = bookingType.toString()
                        modelEventByDate.eventTypeId = eventId
                        modelEventByDate.customerName = customerName
                        modelEventByDate.customerNumber = customerNumber
                        modelEventByDate.customerEmail = customerEmail
                        modelEventByDate.noOfPeople = noOfPeople
                        modelEventByDate.bookingVenue = bookingVenue.toString()
                        modelEventByDate.bookingPackageType = packageType.toString()
                        modelEventByDate.dateOfEnquiry = dateOfEnquiry
                        modelEventByDate.dateOfEvent = dateOfEvent
                        modelEventByDate.startTime = fromTime
                        modelEventByDate.endTime = toTime
                        modelEventByDate.referenceName = referenceName

                        val intent = Intent()
                        intent.putExtra(ConstantKey.KEY_ARRAY, modelEventByDate)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun clearText() {
        etCustomerName.setText("")
        etMobileNumber.setText("")
        etEmail.setText("")

        customerName = ""
        customerNumber = ""
        customerEmail = ""

    }

}
