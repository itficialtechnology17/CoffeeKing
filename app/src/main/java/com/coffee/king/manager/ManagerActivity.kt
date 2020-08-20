package com.coffee.king.manager

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.coffee.king.R
import kotlinx.android.synthetic.main.activity_manager.*

class ManagerActivity : AppCompatActivity() {

    lateinit var context: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manager)
        supportActionBar!!.hide()
        context=this
        initAction()
    }

    private fun initAction() {
        llCheckList.setOnClickListener { startActivity(Intent(this,CheckListActivity::class.java)) }

        llReminder.setOnClickListener { startActivity(Intent(this,ReminderActivity::class.java)) }

        llNegativeFeedback.setOnClickListener { startActivity(Intent(this,NegativeFeedbackActivity::class.java)) }

        llNotes.setOnClickListener { startActivity(Intent(this,NotesActivity::class.java)) }

        llLostFound.setOnClickListener { startActivity(Intent(this,LostFoundActivity::class.java)) }

        llDamage.setOnClickListener { startActivity(Intent(this,DamageActivity::class.java)) }

        llEmployeeDeployment.setOnClickListener { startActivity(Intent(this,EmployeeDeploymentActivity::class.java)) }

        llDutyChart.setOnClickListener { startActivity(Intent(this,DutiesChartActivity::class.java)) }

        ivBack.setOnClickListener{finish()}
    }
}
