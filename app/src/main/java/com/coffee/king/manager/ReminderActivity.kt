package com.coffee.king.manager

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.ReminderAdapter
import com.coffee.king.adminadapter.UserWithTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelReminder
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.ReminderResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_reminder.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReminderActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfUser = ArrayList<ModelUser>()
    var arrOfReminder = ArrayList<ModelReminder>()
    lateinit var userWithTypeAdapter: UserWithTypeAdapter
    lateinit var reminderAdapter: ReminderAdapter
    lateinit var calendar: Calendar
    lateinit var colorRandom: IntArray


    var reminderDate = ""
    var reminderTime = ""
    var responsibleId = ""
    var notes = ""
    var isDateSelect = false
    var isTimeSelect = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminder)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()

    }

    private fun initDefine() {
        colorRandom = context.resources.getIntArray(R.array.colorBG)

        getReminder()
        getUser()

        calendar = Calendar.getInstance()


    }

    private fun initAction() {

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tvReminderDate.text = sdf.format(calendar.time)
                reminderDate = tvReminderDate.text.toString()
                isDateSelect = true
            }

        val fromTimeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            tvReminderTime.text = SimpleDateFormat("HH:mm a").format(calendar.time)
            reminderTime = tvReminderTime.text.toString()
            isTimeSelect = true
        }


        ivBack.setOnClickListener {
            onBackPressed()
        }

        rlUser.setOnClickListener {
            if (arrOfUser.isEmpty()) {
                getUser()
            }
            if (progress.visibility != View.VISIBLE) {
                if (viewUser.visibility == View.GONE) {
                    viewUser.visibility = View.VISIBLE
                    ivUArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewUser.visibility = View.GONE
                    ivUArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }

        llDateOfReminder.setOnClickListener {
            DatePickerDialog(
                this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        llTimeOfReminder.setOnClickListener {
            TimePickerDialog(
                this,
                fromTimeListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnSubmit.setOnClickListener {
            notes = etNotes.text.toString()
            if (Utils.isOnline(context)) {
                if (!isDateSelect) {
                    Toast.makeText(context, "Please select date", Toast.LENGTH_SHORT).show()
                } else if (!isTimeSelect) {
                    Toast.makeText(context, "Please select time", Toast.LENGTH_SHORT).show()
                } else if (responsibleId == "") {
                    Toast.makeText(context, "Please select responsible person", Toast.LENGTH_SHORT)
                        .show()
                } else if (notes == "") {
                    Toast.makeText(context, "Reminder Notes is empty", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    addReminder()
                }
            } else {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

        btnAdd.setOnClickListener {
            rlList.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
        }

    }

    override fun onBackPressed() {
        if (rlContent.visibility==View.VISIBLE) {
            rlList.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
        } else {
            super.onBackPressed()
        }

    }

    private fun getUser() {

        progress.visibility = View.VISIBLE
        ivUArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUser()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    rvUser.visibility = View.VISIBLE
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    setUserAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setUserAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUser.layoutManager = layoutManager
        userWithTypeAdapter = UserWithTypeAdapter(context, arrOfUser, this)
        rvUser.adapter = userWithTypeAdapter
    }

    private fun addReminder() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addReminder(userId, reminderDate, reminderTime, responsibleId, notes)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    rlList.visibility = View.VISIBLE
                    rlContent.visibility = View.GONE
                    getReminder()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.rlBG) {
            viewUser.visibility = View.GONE
            responsibleId = arrOfUser[position].userId
            tvUser.text =
                arrOfUser[position].userName + "(" + arrOfUser[position].userTypeName + ")"
        }
    }

    private fun getReminder() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getReminder(userId)

        call.enqueue(object : Callback<ReminderResponse> {
            override fun onResponse(
                call: Call<ReminderResponse>,
                response: Response<ReminderResponse>
            ) {
                if (response.body() != null) {
                    rvUser.visibility = View.VISIBLE
                    val reminderResponse = response.body() as ReminderResponse
                    arrOfReminder = reminderResponse.arrOfReminder
                    setReminderAdapter()
                }
            }

            override fun onFailure(call: Call<ReminderResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setReminderAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvReminder.layoutManager = layoutManager
        reminderAdapter = ReminderAdapter(context, arrOfReminder, colorRandom, this)
        rvReminder.adapter = reminderAdapter
    }
}
