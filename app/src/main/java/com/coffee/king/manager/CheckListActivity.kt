package com.coffee.king.manager

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.ManagerCheckListAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCheckList
import com.coffee.king.responseCallback.CheckListResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_check_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CheckListActivity : AppCompatActivity(), ItemClickCallback {

    lateinit var context: Context
    var yesterday = ""
    var today = ""
    var selectedDate = ""
    var arrOfCheckList = ArrayList<ModelCheckList>()
    lateinit var managerCheckListAdapter: ManagerCheckListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_list)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }


    private fun initDefine() {
        yesterday = Utils.getYesterdayDateString()
        today = Utils.getCurrentDate()
        selectedDate = today
        tvPreviousDate.text = yesterday
        tvTodayDate.text = today
        getManagerCheckList()

    }

    private fun initAction() {

        ivBack.setOnClickListener { finish() }

        llDateOfYesterday.setOnClickListener {
            llDateOfYesterday.setBackgroundResource(R.drawable.ic_bg_corner_selected)
            llDateOfToday.setBackgroundResource(R.drawable.ic_bg_corner_white)
            tvPreviousDate.setTextColor(Color.WHITE)
            tvTodayDate.setTextColor(Color.BLACK)
            selectedDate = yesterday
            getManagerCheckList()
        }

        llDateOfToday.setOnClickListener {
            llDateOfToday.setBackgroundResource(R.drawable.ic_bg_corner_selected)
            llDateOfYesterday.setBackgroundResource(R.drawable.ic_bg_corner_white)
            tvPreviousDate.setTextColor(Color.BLACK)
            tvTodayDate.setTextColor(Color.WHITE)
            selectedDate = today
            getManagerCheckList()
        }

        btnSubmit.setOnClickListener {
            if (Utils.isOnline(context)) {
                val serverDate=Utils.getPref(context, ConstantKey.KEY_SERVER_DATE, "")
                val d1=Utils.getCurrentDate()
                val d2=Utils.getYesterdayDateString()
//                addToCheckList()
                if(serverDate==d1 || serverDate==d2){
                    addToCheckList()
                }else{
                    Toast.makeText(context,"Please make sure your phone date is correct",Toast.LENGTH_LONG).show()
                }

            }else{
                Toast.makeText(context,"No Internet Connection",Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun getManagerCheckList() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getManagerCheckList(userId, selectedDate)

        call.enqueue(object : Callback<CheckListResponse> {
            override fun onResponse(
                call: Call<CheckListResponse>,
                response: Response<CheckListResponse>
            ) {
                if (response.body() != null) {
                    val checkListResponse = response.body() as CheckListResponse
                    arrOfCheckList= ArrayList()
                    arrOfCheckList = checkListResponse.arrOfCheckList
                    setCheckListAdapter()
                }
            }

            override fun onFailure(call: Call<CheckListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setCheckListAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvManagerCheckList.layoutManager = layoutManager
        managerCheckListAdapter = ManagerCheckListAdapter(context, arrOfCheckList, this)
        rvManagerCheckList.adapter = managerCheckListAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if(view.id==R.id.cbTick){
            if(arrOfCheckList[position].isSelected){
                arrOfCheckList[position].isSelected=false
                arrOfCheckList[position].isChecked="0"
            }else{
                arrOfCheckList[position].isSelected=true
                arrOfCheckList[position].isChecked="1"
            }
        }
    }

    private fun addToCheckList() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!
        val selectionJson = Gson().toJson(arrOfCheckList)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addMangerCheckList(userId, selectionJson, selectedDate)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    rlLoader.visibility = View.GONE
                    btnSubmit.visibility = View.VISIBLE
                    val responseStatusCode = response.body() as ResponseStatusCode
                    if (responseStatusCode.statusCode == 101) {
                        Snackbar.make(rlLoader,responseStatusCode.message,2000).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

}
