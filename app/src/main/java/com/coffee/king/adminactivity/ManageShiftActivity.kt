package com.coffee.king.adminactivity

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adminadapter.AdminShiftAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelShift
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.ShiftResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_manage_shift.*
import kotlinx.android.synthetic.main.cell_of_admin_shift.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ManageShiftActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var shiftId=""
    var shiftName=""
    var startTime=""
    var endTime=""

    var arrOfShift=ArrayList<ModelShift>()
    var position=-1

    lateinit var calendar: Calendar

    lateinit var adminShiftAdapter: AdminShiftAdapter

    var isFromTimeSelected=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_shift)
        context=this
        supportActionBar!!.hide()
        initDefine()
        initAction()
    }

    private fun initDefine() {
        calendar = Calendar.getInstance()
        getShift()
    }

    private fun initAction() {


        val fromTimeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            tvFromTime.text = SimpleDateFormat("HH:mm a").format(calendar.time)
            startTime = tvFromTime.text.toString()

        }

        val toTimeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            tvEndTime.text = SimpleDateFormat("HH:mm a").format(calendar.time)
            endTime = tvEndTime.text.toString()
        }

        ivAdd.setOnClickListener {
            rlAddUpdate.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
            ivAdd.visibility = View.GONE
            btnAddUpdate.text = "Add"
            shiftId=""
            etShiftName.setText("")

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

        btnAddUpdate.setOnClickListener {

            shiftName = etShiftName.text.toString()

            if (shiftName.isEmpty()) {
                Toast.makeText(context, "Enter Shift Name", Toast.LENGTH_SHORT).show()
            }else {
                addUpdateShift()
            }
        }
    }

    private fun getShift() {

        slRecycle.startShimmerAnimation()
        slRecycle.visibility=View.VISIBLE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getShift()

        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(
                call: Call<ShiftResponse>,
                response: Response<ShiftResponse>
            ) {

                if (response.body() != null) {
                    slRecycle.visibility=View.GONE
                    rvShift.visibility=View.VISIBLE
                    val shiftResponse = response.body() as ShiftResponse
                    arrOfShift = shiftResponse.arrOfShift
                    setShiftAdapter()
                }
            }

            override fun onFailure(call: Call<ShiftResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun addUpdateShift() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addAShift(
            userId, shiftName,startTime,endTime)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                rlContent.visibility=View.VISIBLE
                rlAddUpdate.visibility=View.GONE
                ivAdd.visibility=View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    getShift()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setShiftAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvShift.layoutManager = layoutManager
        adminShiftAdapter = AdminShiftAdapter(context, arrOfShift, this)
        rvShift.adapter = adminShiftAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.ivEdit) {
            setEditData(position)
        } else if (view.id == R.id.ivDelete) {
            onClickDelete(position)
        }
    }

    private fun setEditData(position: Int) {
        this.position=position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        shiftId = arrOfShift[position].shiftId
        etShiftName.setText(arrOfShift[position].shiftName)

        tvFromTime.text = arrOfShift[position].shiftStartTime
        tvEndTime.text = arrOfShift[position].shiftEndTime
        startTime = arrOfShift[position].shiftStartTime
        endTime = arrOfShift[position].shiftEndTime

        switchUser.isChecked = arrOfShift[position].status=="1"

        btnAddUpdate.text = "Update"
    }

    private fun onClickDelete(position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Sure to delete shift "+arrOfShift[position].shiftName+" ?")
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteShift(position)
            }
            .create()
            .show()
    }

    private fun deleteShift(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteShift(arrOfShift[position].shiftId)
        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    if (responseStatusCode.statusCode == 101) {
                        Toast.makeText(
                            context,
                            responseStatusCode.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        arrOfShift.removeAt(position)
                        adminShiftAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            context,
                            responseStatusCode.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}
