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
import com.coffee.king.adminadapter.AdminEventTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelEventType
import com.coffee.king.responseCallback.EventsResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_manage_category.btnAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.ivAdd
import kotlinx.android.synthetic.main.activity_manage_category.ivBack
import kotlinx.android.synthetic.main.activity_manage_category.rlAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.rlContent
import kotlinx.android.synthetic.main.activity_manage_category.rlLoader
import kotlinx.android.synthetic.main.activity_manage_category.slRecycle
import kotlinx.android.synthetic.main.activity_manage_event.*
import kotlinx.android.synthetic.main.activity_manage_event.rvEventType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageEventActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfEventType = ArrayList<ModelEventType>()
    lateinit var adminEventTypeAdapter: AdminEventTypeAdapter

    var position=0
    var eventTypeId = ""
    var eventName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_event)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if (Utils.isOnline(context)) {
            getEvents()
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
            eventTypeId=""
            etEventName.setText("")

        }

        btnAddUpdate.setOnClickListener {

            eventName = etEventName.text.toString()

            if (eventName.isEmpty()) {
                Toast.makeText(context, "Enter Event Name", Toast.LENGTH_SHORT).show()
            }else {
                addUpdateEvent()
            }
        }
    }

    private fun getEvents() {

        slRecycle.startShimmerAnimation()
        slRecycle.visibility=View.VISIBLE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getEvents()

        call.enqueue(object : Callback<EventsResponse> {
            override fun onResponse(
                call: Call<EventsResponse>,
                response: Response<EventsResponse>
            ) {
                if (response.body() != null) {
                    slRecycle.visibility=View.GONE
                    rvEventType.visibility=View.VISIBLE
                    val eventsResponse = response.body() as EventsResponse
                    arrOfEventType = eventsResponse.arrOfModelEventType
                    setEventAdapter()
                }
            }

            override fun onFailure(call: Call<EventsResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setEventAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvEventType.layoutManager = layoutManager
        adminEventTypeAdapter = AdminEventTypeAdapter(context, arrOfEventType, this)
        rvEventType.adapter = adminEventTypeAdapter
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
            .setMessage("Sure to delete event "+arrOfEventType[position].eventName+" ?")
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteEventType(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position=position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        eventTypeId = arrOfEventType[position].eventId
        etEventName.setText(arrOfEventType[position].eventName)
        btnAddUpdate.text = "Update"
    }

    private fun addUpdateEvent() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateEvent(
            userId, eventTypeId,eventName)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                rlContent.visibility=View.VISIBLE
                rlAddUpdate.visibility=View.GONE
                ivAdd.visibility=View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if(eventTypeId==""){
                        val modelEventType=ModelEventType()
                        modelEventType.eventId=responseStatusCode.id
                        modelEventType.eventName=eventName
                        arrOfEventType.add(modelEventType)
                        adminEventTypeAdapter.notifyDataSetChanged()
                    }else{
                        arrOfEventType[position].eventName=eventName
                        adminEventTypeAdapter.notifyDataSetChanged()
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


    private fun deleteEventType(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteEventType(arrOfEventType[position].eventId)
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
                        arrOfEventType.removeAt(position)
                        adminEventTypeAdapter.notifyDataSetChanged()
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
