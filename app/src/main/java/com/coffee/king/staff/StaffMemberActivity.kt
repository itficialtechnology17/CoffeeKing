package com.coffee.king.staff

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.activity.LoginActivity
import com.coffee.king.staffadapter.AssignDutyAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelAssignDuty
import com.coffee.king.responseCallback.AssignDutyResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_notes.*
import kotlinx.android.synthetic.main.activity_staff_member.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.ArrayList

class StaffMemberActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfAssignDuty=ArrayList<ModelAssignDuty>()
    lateinit var assignDutyAdapter: AssignDutyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_staff_member)
        supportActionBar!!.hide()
        context=this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        tvMemberName.text=Utils.getPref(context,ConstantKey.KEY_USER_NAME,"")

        getDutyList()
    }

    private fun getDutyList() {
        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getDutyList(userId)

        call.enqueue(object : Callback<AssignDutyResponse> {
            override fun onResponse(
                call: Call<AssignDutyResponse>,
                response: Response<AssignDutyResponse>
            ) {
                if (response.body() != null) {
                    val assignDutyResponse = response.body() as AssignDutyResponse
                    arrOfAssignDuty = assignDutyResponse.arrOfAssignDuty
                    setDutyAdapter()
                }
            }

            override fun onFailure(call: Call<AssignDutyResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun initAction(){
        ivLogout.setOnClickListener{
            confirmLogout()
        }
        llEmployeeDeployment.setOnClickListener {
            startActivity(Intent(this,MyScheduleActivity::class.java))
        }

        llDutyChart.setOnClickListener {
            startActivity(Intent(this,MyDutyActivity::class.java))
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

    private fun setDutyAdapter(){
        val layoutManager = LinearLayoutManager(context,RecyclerView.VERTICAL,false)
        rvAssignDuty.layoutManager = layoutManager
        assignDutyAdapter = AssignDutyAdapter(context, arrOfAssignDuty, this)
        rvAssignDuty.adapter = assignDutyAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if(view.id==R.id.rlCellBG){
            if(arrOfAssignDuty[position].assignDutyType=="1" || arrOfAssignDuty[position].assignDutyType=="2"){
                startActivity(Intent(this,DailyCheckActivity::class.java).putExtra(ConstantKey.KEY_MODEL,arrOfAssignDuty[position]))
            }else if(arrOfAssignDuty[position].assignDutyType=="3" ){
                startActivity(Intent(this,GroomingActivity::class.java).putExtra(ConstantKey.KEY_MODEL,arrOfAssignDuty[position]))
            }
        }
    }
}
