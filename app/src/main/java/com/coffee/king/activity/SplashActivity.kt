package com.coffee.king.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.coffee.king.R
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.staff.StaffMemberActivity
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {

    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spash)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar!!.hide()
        context = this
        Utils.getSetDeviceIdFromDevice(this)
        Handler().postDelayed({
            if (Utils.getPref(context, ConstantKey.KEY_IS_LOGIN, false) == true) {
                val userType = Utils.getPref(context, ConstantKey.KEY_USER_TYPE, "")
                if (userType == "Admin" || userType == "Manager" || userType == "admin" || userType == "manager") {
                    getServerDate(1)
                } else {
                    getServerDate(2)
                }
            } else {
                startActivity(Intent(context, LoginActivity::class.java))
                finish()
            }
        }, 1000)

    }

    private fun getServerDate(i: Int) {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getServerDate()

        call.enqueue(object : Callback<String> {
            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                if (response.body() != null) {
                    val stringDate = response.body() as String
                    Utils.setPref(context, ConstantKey.KEY_SERVER_DATE, stringDate)
                    val today = Utils.getCurrentDate()
                    if (stringDate != today) {
                        Toast.makeText(context,"Please make sure your phone date is correct",Toast.LENGTH_LONG).show()
                    } else {
                        if (i == 1) {
                            startActivity(Intent(context, MainActivity::class.java))
                        } else {
                            startActivity(Intent(context, StaffMemberActivity::class.java))
                        }
                    }
                }else{
                    if (i == 1) {
                        startActivity(Intent(context, MainActivity::class.java))
                    } else {
                        startActivity(Intent(context, StaffMemberActivity::class.java))
                    }
                }
                finish()
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Toast.makeText(context,"Please make sure your phone date is correct",Toast.LENGTH_LONG).show()
                if (i == 1) {
                    startActivity(Intent(context, MainActivity::class.java))
                } else {
                    startActivity(Intent(context, StaffMemberActivity::class.java))
                }
                finish()
                t.printStackTrace()
            }
        })
    }
}
