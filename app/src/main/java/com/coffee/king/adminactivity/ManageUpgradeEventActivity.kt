package com.coffee.king.adminactivity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adminadapter.AdminUpgradeEventAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelUpgradeEvent
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UpgradeEventResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_manage_category.btnAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.etPrice
import kotlinx.android.synthetic.main.activity_manage_category.ivAdd
import kotlinx.android.synthetic.main.activity_manage_category.ivBack
import kotlinx.android.synthetic.main.activity_manage_category.rlAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.rlContent
import kotlinx.android.synthetic.main.activity_manage_category.rlLoader
import kotlinx.android.synthetic.main.activity_manage_category.slRecycle
import kotlinx.android.synthetic.main.activity_manage_upgrade_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageUpgradeEventActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfModelUpgradeEvent = ArrayList<ModelUpgradeEvent>()
    lateinit var adminUpgradeEventAdapter: AdminUpgradeEventAdapter

    var position = 0
    var upgradeEventId = ""
    var eventTitle = ""
    var eventPrice = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_upgrade_event)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if (Utils.isOnline(context)) {
            getUpgradeEventList()
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initDefine() {

    }

    private fun initAction() {
        ivBack.setOnClickListener {
            if (rlAddUpdate.visibility == View.VISIBLE) {
                rlAddUpdate.visibility = View.GONE
                rlContent.visibility = View.VISIBLE
                ivAdd.visibility = View.VISIBLE
            } else {
                finish()
            }
        }

        ivAdd.setOnClickListener {
            rlAddUpdate.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
            ivAdd.visibility = View.GONE
            btnAddUpdate.text = "Add"
            etUpgradeEventTitle.setText("")
            etPrice.setText("")

            upgradeEventId = ""
        }

        btnAddUpdate.setOnClickListener {

            eventTitle = etUpgradeEventTitle.text.toString()
            eventPrice = etPrice.text.toString()


            if (eventTitle.isEmpty()) {
                Toast.makeText(context, "Enter Event Title", Toast.LENGTH_SHORT).show()
            } else if (eventPrice.isEmpty()) {
                Toast.makeText(context, "Enter Price", Toast.LENGTH_SHORT).show()
            } else {
                addUpdateUpgradeEvent()
            }
        }
    }


    private fun getUpgradeEventList() {

        slRecycle.startShimmerAnimation()
        slRecycle.visibility = View.VISIBLE


        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUpgradeEventList()

        call.enqueue(object : Callback<UpgradeEventResponse> {
            override fun onResponse(
                call: Call<UpgradeEventResponse>,
                response: Response<UpgradeEventResponse>
            ) {

                if (response.body() != null) {
                    slRecycle.startShimmerAnimation()
                    slRecycle.visibility = View.GONE
                    rvUpgradeEvent.visibility = View.VISIBLE

                    val upgradeEventResponse = response.body() as UpgradeEventResponse
                    arrOfModelUpgradeEvent = upgradeEventResponse.arrOfUpgradeEvent
                    setAdapter()
                }
            }

            override fun onFailure(call: Call<UpgradeEventResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUpgradeEvent.layoutManager = layoutManager
        adminUpgradeEventAdapter =
            AdminUpgradeEventAdapter(context, arrOfModelUpgradeEvent, this)
        rvUpgradeEvent.adapter = adminUpgradeEventAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.ivEdit) {
            setEditData(position)
        } else if (view.id == R.id.ivDelete) {
            onClickDelete(position)
        }
    }

    private fun onClickDelete(position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Sure to delete " + arrOfModelUpgradeEvent[position].upgradeEventName + "?")
            .setNegativeButton(android.R.string.no) { dialog, i ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteUpgradeEvent(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position = position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        upgradeEventId = arrOfModelUpgradeEvent[position].upgradeEventId
        etUpgradeEventTitle.setText(arrOfModelUpgradeEvent[position].upgradeEventName)
        etPrice.setText(arrOfModelUpgradeEvent[position].upgradeEventPrice)

        btnAddUpdate.text = "Update"
    }

    private fun addUpdateUpgradeEvent() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateUpgradeEvent(
            userId, upgradeEventId, eventTitle, eventPrice)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                rlContent.visibility = View.VISIBLE
                rlAddUpdate.visibility = View.GONE
                ivAdd.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (upgradeEventId == "") {
                        val modelUpgradeEvent = ModelUpgradeEvent()
                        modelUpgradeEvent.upgradeEventId = responseStatusCode.id
                        modelUpgradeEvent.upgradeEventName = eventTitle
                        modelUpgradeEvent.upgradeEventPrice = eventPrice

                        arrOfModelUpgradeEvent.add(modelUpgradeEvent)
                        adminUpgradeEventAdapter.notifyDataSetChanged()
                    } else {
                        arrOfModelUpgradeEvent[position].upgradeEventName = eventTitle
                        arrOfModelUpgradeEvent[position].upgradeEventPrice = eventPrice

                        adminUpgradeEventAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }


    private fun deleteUpgradeEvent(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteUpgradeEvent(arrOfModelUpgradeEvent[position].upgradeEventId)
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
                        arrOfModelUpgradeEvent.removeAt(position)
                        adminUpgradeEventAdapter.notifyDataSetChanged()
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

    override fun onBackPressed() {
        if (rlAddUpdate.visibility == View.VISIBLE) {
            rlAddUpdate.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
            ivAdd.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }


}
