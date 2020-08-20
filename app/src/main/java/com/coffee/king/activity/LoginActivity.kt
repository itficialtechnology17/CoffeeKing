package com.coffee.king.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.coffee.king.R
import com.coffee.king.responseCallback.LoginResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.staff.StaffMemberActivity
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        supportActionBar!!.hide()
        context = this
        initAction()
    }

    private fun initAction() {
        btnLogin.setOnClickListener {
            when {
                TextUtils.isEmpty(etUserName.text.toString()) -> Toast.makeText(
                    context, "Please Enter UserName",
                    Toast.LENGTH_SHORT
                ).show()

                TextUtils.isEmpty(etPassword.text.toString()) -> Toast.makeText(
                    context, "Please Enter Password",
                    Toast.LENGTH_SHORT
                ).show()

                else -> login(etUserName.text.toString(), etPassword.text.toString())
            }
        }
    }

    private fun login(userName: String, password: String) {

        rlLoader.visibility = View.VISIBLE
        btnLogin.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.doLogin(userName, password)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {

                if (response.body() != null) {
                    val loginResponse = response.body() as LoginResponse
                    if (loginResponse.responseCode == 101) {
                        Utils.setPref(context, ConstantKey.KEY_IS_LOGIN, true)
                        Utils.setPref(context, ConstantKey.KEY_LOGIN_ID, loginResponse.loginId)
                        Utils.setPref(context, ConstantKey.KEY_USER_NAME, loginResponse.userName)
                        Utils.setPref(context, ConstantKey.KEY_USER_TYPE, loginResponse.userType)
                        Utils.setPref(context, ConstantKey.KEY_IS_PWD, password)

                        val stringDate = loginResponse.currentDate
                        Utils.setPref(context, ConstantKey.KEY_SERVER_DATE, stringDate)
                        val today = Utils.getCurrentDate()

                        if (stringDate != today) {
                            Toast.makeText(
                                context,
                                "Please make sure your phone date is correct",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            finish()
                            if (loginResponse.userType == "Admin" || loginResponse.userType == "Manager") {
                                startActivity(Intent(context, MainActivity::class.java))
                            } else {
                                startActivity(
                                    Intent(
                                        context,
                                        StaffMemberActivity::class.java
                                    )
                                )
                            }
                        }

                    } else if (loginResponse.responseCode == 100) {
                        Toast.makeText(context, loginResponse.responseMessage, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnLogin.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }


}



