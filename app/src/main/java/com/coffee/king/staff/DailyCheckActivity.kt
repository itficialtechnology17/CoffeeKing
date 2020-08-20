package com.coffee.king.staff

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelAssignDuty
import com.coffee.king.model.ModelDailyCheckList
import com.coffee.king.responseCallback.DailyCheckListResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.staffadapter.DailyCheckAdapter
import com.coffee.king.utils.Utils
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_daily_check.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class DailyCheckActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var modelAssignDuty = ModelAssignDuty()
    var arrOfDailyCheckList = ArrayList<ModelDailyCheckList>()
    var arrOfSelected = ArrayList<ModelDailyCheckList>()

    lateinit var dailyCheckAdapter: DailyCheckAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_check)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()

    }

    private fun initDefine() {
        modelAssignDuty = intent.getSerializableExtra(ConstantKey.KEY_MODEL) as ModelAssignDuty
        tvTitle.text = modelAssignDuty.assignDutyText
        if (modelAssignDuty.assignDutyType == "1") {
            getCheckListMaster(modelAssignDuty.assignDutyType)
        }else{
            getUserCheckListMaster(modelAssignDuty.assignDutyType)
        }
    }

    private fun initAction() {
        ivBack.setOnClickListener {
            finish()
        }

        btnSubmit.setOnClickListener {

            arrOfSelected.clear()
            for(i in 0 until arrOfDailyCheckList.size){
                if(arrOfDailyCheckList[i].isSelected){
                    arrOfSelected.add(arrOfDailyCheckList[i])
                }
            }

            addDailyCheckList()
        }
    }

    private fun addDailyCheckList() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val addedBy= Utils.getPref(context, ConstantKey.KEY_LOGIN_ID,"")!!

        var selectionJson="";
        selectionJson = if(modelAssignDuty.assignDutyType=="1"){
            Gson().toJson(arrOfSelected)
        }else{
            Gson().toJson(arrOfDailyCheckList)
        }

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addDailyCheckList(addedBy,selectionJson)

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

                    arrOfSelected.clear()

                    for(i in 0 until arrOfDailyCheckList.size){
                        arrOfDailyCheckList[i].isSelected=false
                    }
                    dailyCheckAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
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

    private fun getUserCheckListMaster(assignDutyType: String) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUserCheckListMaster(assignDutyType)

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
        rvCheckListMaster.layoutManager = layoutManager
        dailyCheckAdapter = DailyCheckAdapter(context, arrOfDailyCheckList, this)
        rvCheckListMaster.adapter = dailyCheckAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        arrOfDailyCheckList[position].isSelected = !arrOfDailyCheckList[position].isSelected
        dailyCheckAdapter.notifyDataSetChanged()
    }
}
