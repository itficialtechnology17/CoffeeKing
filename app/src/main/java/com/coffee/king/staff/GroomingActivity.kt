package com.coffee.king.staff

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.InChargeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelAssignDuty
import com.coffee.king.model.ModelDailyCheckList
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.DailyCheckListResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.staffadapter.DailyCheckAdapter
import com.coffee.king.staffadapter.GroomingUserCheckedAdapter
import com.coffee.king.utils.Utils
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_grooming.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GroomingActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var modelAssignDuty = ModelAssignDuty()
    var arrOfDailyCheckList = ArrayList<ModelDailyCheckList>()
    lateinit var dailyCheckAdapter: DailyCheckAdapter
    private lateinit var userAdapter: InChargeAdapter
    private lateinit var groomingUserCheckedAdapter: GroomingUserCheckedAdapter

    var arrOfUser = ArrayList<ModelUser>()

    var employeeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grooming)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        modelAssignDuty = intent.getSerializableExtra(ConstantKey.KEY_MODEL) as ModelAssignDuty
        tvTitle.text = modelAssignDuty.assignDutyText
        getCheckListMaster(modelAssignDuty.assignDutyType)
        getGroomingCheckedUser()
        getUser()
    }

    private fun initAction() {
        ivBack.setOnClickListener {
            onBackPressed()
        }

        rlInChargeUser.setOnClickListener {
            if (arrOfUser.isEmpty()) {
                getUser()
            }
            if (progressInCharge.visibility != View.VISIBLE) {
                if (viewInChargeUser.visibility == View.GONE) {
                    viewInChargeUser.visibility = View.VISIBLE
                    ivIArrow.setImageResource(R.drawable.ic__arrow_up)
                    rvCheckedGrooming.visibility=View.GONE
                } else {
                    viewInChargeUser.visibility = View.GONE
                    ivIArrow.setImageResource(R.drawable.ic_arrow_down)
                    rvCheckedGrooming.visibility=View.VISIBLE
                }
            }
        }

        btnSubmit.setOnClickListener {
            addGroomingCheckList()
        }
    }

    override fun onBackPressed() {
        if (llCheckGrooming.visibility == View.VISIBLE) {
            llCheckGrooming.visibility = View.GONE
            rvCheckedGrooming.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }

    private fun addGroomingCheckList() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val addedBy = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        val selectionJson = Gson().toJson(arrOfDailyCheckList)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addGroomingCheckList(addedBy, employeeId, selectionJson)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    rlLoader.visibility = View.GONE
                    btnSubmit.visibility = View.VISIBLE
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()


                    for (i in 0 until arrOfDailyCheckList.size) {
                        arrOfDailyCheckList[i].isSelected = false
                    }
                    dailyCheckAdapter.notifyDataSetChanged()

                    llCheckGrooming.visibility = View.GONE
                    rvCheckedGrooming.visibility = View.VISIBLE
                    getGroomingCheckedUser();
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun getUser() {

        progressInCharge.visibility = View.VISIBLE
        ivIArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUser()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                progressInCharge.visibility = View.GONE
                ivIArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    setUserAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progressInCharge.visibility = View.GONE
                ivIArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun getGroomingCheckedUser() {

        progressInCharge.visibility = View.VISIBLE
        ivIArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getGroomingCheckedUser()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                progressInCharge.visibility = View.GONE
                ivIArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    setCheckedGroomingAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progressInCharge.visibility = View.GONE
                ivIArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }


    private fun setCheckedGroomingAdapter() {
        rvCheckedGrooming.visibility = View.VISIBLE
        llCheckGrooming.visibility=View.GONE
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCheckedGrooming.layoutManager = layoutManager
        groomingUserCheckedAdapter = GroomingUserCheckedAdapter(context, arrOfUser, this)
        rvCheckedGrooming.adapter = groomingUserCheckedAdapter
    }

    private fun setUserAdapter() {
        rvUser.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUser.layoutManager = layoutManager
        userAdapter = InChargeAdapter(context, arrOfUser, this)
        rvUser.adapter = userAdapter
    }


    private fun getCheckListMaster(assignDutyType: String) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCheckListMaster(assignDutyType)

        call.enqueue(object : Callback<DailyCheckListResponse> {
            override fun onResponse(
                call: Call<DailyCheckListResponse>,
                response: Response<DailyCheckListResponse>
            ) {
                if (response.body() != null) {
                    val dailyCheckListResponse = response.body() as DailyCheckListResponse
                    arrOfDailyCheckList = dailyCheckListResponse.arrOfDailyCheckList
                    setDailyCheckListAdapter()
                }
            }

            override fun onFailure(call: Call<DailyCheckListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setDailyCheckListAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvGrooming.layoutManager = layoutManager
        dailyCheckAdapter = DailyCheckAdapter(context, arrOfDailyCheckList, this)
        rvGrooming.adapter = dailyCheckAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        when {
            view.id == R.id.rlBGInCharge -> {
                if (!arrOfUser[position].isSelected) {
                    for (i in 0 until arrOfUser.size) {
                        arrOfUser[i].isSelected = false
                    }
                    tvInChargeUser.text = arrOfUser[position].userName
                    arrOfUser[position].isSelected = true
                    employeeId = arrOfUser[position].userId
                    viewInChargeUser.visibility = View.GONE
                    ivIArrow.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    arrOfUser[position].isSelected = false
                    employeeId = ""
                    tvInChargeUser.text = ""
                }

                userAdapter.notifyDataSetChanged()
                llCheckGrooming.visibility = View.VISIBLE
                rvCheckedGrooming.visibility = View.GONE
            }

            view.id == R.id.rltBGDuty -> {
                arrOfDailyCheckList[position].isSelected = !arrOfDailyCheckList[position].isSelected
                dailyCheckAdapter.notifyDataSetChanged()
            }

        }
    }
}
