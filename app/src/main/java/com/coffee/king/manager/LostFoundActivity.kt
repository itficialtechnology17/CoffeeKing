package com.coffee.king.manager

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.LostFoundAdapter
import com.coffee.king.adminadapter.UserWithTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelLostFound
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.LostFoundResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_lost_found.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class LostFoundActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfUser = ArrayList<ModelUser>()
    var arrOfLostFound = ArrayList<ModelLostFound>()
    private lateinit var userWithTypeAdapter: UserWithTypeAdapter
    lateinit var lostFoundAdapter: LostFoundAdapter
    lateinit var calendar: Calendar

    var lostFoundItemId = ""
    var itemName = ""
    var contact = ""
    var date = ""
    var time = ""
    var name = ""
    var contactNumber = ""
    var foundTimeStamp = ""
    var claimBy = ""
    var status = "1"
    var isDateSelect = false
    var isTimeSelect = false
    var isFoundClaimCheck=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lost_found)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        calendar = Calendar.getInstance()
        getLostFound()
        getUser()
    }

    private fun initAction() {

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

        rgType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCustomer -> {
                    etName.visibility = View.VISIBLE
                    llUser.visibility = View.GONE
                }
                R.id.rbStaff -> {
                    etName.visibility = View.GONE
                    llUser.visibility = View.VISIBLE
                }
            }
        }

        rgStatus.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbFound -> {
                    status = "2"
                    isFoundClaimCheck=true
                }
                R.id.rbClaim -> {
                    status = "3"
                    isFoundClaimCheck=true
                }
            }
        }

        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tvDate.text = sdf.format(calendar.time)
                date = tvDate.text.toString()
                isDateSelect = true
            }

        val fromTimeListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            tvTime.text = SimpleDateFormat("HH:mm a").format(calendar.time)
            time = tvTime.text.toString()
            isTimeSelect = true
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
            itemName = etLostFoundItem.text.toString()
            name = etName.text.toString()
            contactNumber = etContact.text.toString()
            claimBy = etClaimBy.text.toString()


            if (lostFoundItemId.isEmpty()) {
                when {
                    itemName.isEmpty() -> Toast.makeText(
                        context,
                        "Please Enter Item Name",
                        Toast.LENGTH_SHORT
                    ).show()

                    name.isEmpty() -> Toast.makeText(
                        context,
                        "Please Enter Name",
                        Toast.LENGTH_SHORT
                    ).show()

                    contactNumber.isEmpty() -> Toast.makeText(
                        context,
                        "Please Enter Contact Number",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> {
                        addLostItem()
                    }
                }
            } else {
                if (claimBy.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Claim By Is Empty",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if(!isFoundClaimCheck) {
                    Toast.makeText(
                        context,
                        "Please select status",
                        Toast.LENGTH_SHORT
                    ).show()
                }else{
                    addFoundOrClaimItem()
                }
            }
        }

        btnAdd.setOnClickListener {
            llOnFound.visibility = View.GONE
            llOnLost.visibility = View.VISIBLE
            rlList.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if(rlContent.visibility==View.VISIBLE){
            rlList.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
        }else{
            super.onBackPressed()
        }

    }
    private fun addLostItem() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addLostItem(userId, itemName, date, time, name, contactNumber)

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
                    getLostFound()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun addFoundOrClaimItem() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addFoundOrClaimItem(
                userId,
                lostFoundItemId,
                claimBy,
                status
            )

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
                    etClaimBy.setText("")
                    etLostFoundItem.setText("")
                    rbFound.isChecked=false
                    rbClaim.isChecked=false
                    isFoundClaimCheck=false
                    lostFoundItemId=""

                    getLostFound()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun getLostFound() {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getLostFound()

        call.enqueue(object : Callback<LostFoundResponse> {
            override fun onResponse(
                call: Call<LostFoundResponse>,
                response: Response<LostFoundResponse>
            ) {
                if (response.body() != null) {
                    val lostFoundResponse = response.body() as LostFoundResponse
                    arrOfLostFound = lostFoundResponse.arrOfLostFound
                    progress1.visibility = View.GONE
                    progress2.visibility = View.GONE
                    tvNoOfLost.visibility = View.VISIBLE
                    tvNoOfFound.visibility = View.VISIBLE
                    setLostFoundAdapter()
                    setLostFound()
                }
            }

            override fun onFailure(call: Call<LostFoundResponse>, t: Throwable) {
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setLostFound() {
        var lostCount = 0
        var foundCount = 0
        for (i in 0 until arrOfLostFound.size) {
            if (arrOfLostFound[i].status.toInt() == 1) {
                lostCount += 1
            } else if (arrOfLostFound[i].status.toInt() == 2) {
                foundCount += 1
            }
        }
        tvNoOfFound.text = foundCount.toString()
        tvNoOfLost.text = lostCount.toString()
    }

    private fun setLostFoundAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvLostFound.layoutManager = layoutManager
        lostFoundAdapter = LostFoundAdapter(context, arrOfLostFound, this)
        rvLostFound.adapter = lostFoundAdapter
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

    @SuppressLint("MissingPermission")
    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.rlBG) {
            if (!arrOfUser[position].isSelected) {
                arrOfUser[position].isSelected = true
                name = arrOfUser[position].userName
                tvUser.text = name
                contact = arrOfUser[position].userMobile
                etContact.setText(contact)
            } else {
                clearFilter()
            }
            ivUArrow.setImageResource(R.drawable.ic_arrow_down)
            viewUser.visibility = View.GONE
            userWithTypeAdapter.notifyDataSetChanged()
        } else if (view.id == R.id.tvMobileNo) {
            val mobileNo=arrOfLostFound[position].contactNumber
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CALL_PHONE), 0)
            } else {
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$mobileNo")))
            }
        } else if (view.id == R.id.rlContentBG) {
            setEditData(position)
        }
    }

    private fun setEditData(position: Int) {
        rlList.visibility = View.GONE
        rlContent.visibility = View.VISIBLE
        llOnFound.visibility = View.VISIBLE
        llOnLost.visibility = View.GONE
        etLostFoundItem.setText(arrOfLostFound[position].itemName)
        lostFoundItemId = arrOfLostFound[position].lostFoundId
        if(arrOfLostFound[position].status=="2"){
            isFoundClaimCheck=true
            status="2"
            rbFound.isChecked=true
        }
    }

    private fun clearFilter() {
        for (i in 0 until arrOfUser.size) {
            arrOfUser[i].isSelected = false
        }
    }
}
