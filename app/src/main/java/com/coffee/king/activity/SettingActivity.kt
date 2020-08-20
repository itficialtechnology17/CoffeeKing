package com.coffee.king.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.coffee.king.R
import com.coffee.king.adminactivity.*
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
//        val userId=Utils.getPref(context,ConstantKey.KEY_LOGIN_ID,"")
        val userType=Utils.getPref(context,ConstantKey.KEY_USER_TYPE,"")
        if(userType=="Admin"){
            rlUser.visibility=View.VISIBLE
        }else{
            rlUser.visibility=View.GONE

        }
    }

    private fun initAction() {
        ivBack.setOnClickListener {
            finish()
        }

        rlUser.setOnClickListener {
            startActivity(Intent(context, ManageUserActivity::class.java))
        }

        rlCheckList.setOnClickListener{
            startActivity(Intent(context, ManageCheckListActivity::class.java))
        }

        rlCategory.setOnClickListener {
            startActivity(Intent(context, ManageCategoryActivity::class.java))
        }

        rlFood.setOnClickListener {
            startActivity(Intent(context, ManageFoodActivity::class.java))
        }

        rlEventType.setOnClickListener {
            startActivity(Intent(context, ManageEventActivity::class.java))
        }

        rlUpgradeEvent.setOnClickListener {
            startActivity(Intent(context, ManageUpgradeEventActivity::class.java))
        }

        rlCouplePackage.setOnClickListener {
            startActivity(Intent(context, ManageCouplePackageActivity::class.java))
        }

        rlShift.setOnClickListener {
            startActivity(Intent(context, ManageShiftActivity::class.java))
        }


        rlLogout.setOnClickListener {
            confirmLogout()
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
}
