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
import com.coffee.king.adminadapter.AdminChecklistAdapter
import com.coffee.king.adminadapter.AdminEventTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCheckList
import com.coffee.king.model.ModelEventType
import com.coffee.king.responseCallback.CheckListResponse
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
import kotlinx.android.synthetic.main.activity_manage_check_list.*
import kotlinx.android.synthetic.main.activity_manage_event.*
import kotlinx.android.synthetic.main.activity_manage_event.rvEventType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCheckListActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context


    var arrOfCheckList = ArrayList<ModelCheckList>()
    lateinit var adminChecklistAdapter: AdminChecklistAdapter

    var position=0
    var checkListId = ""
    var checkListName = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_check_list)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if (Utils.isOnline(context)) {
            getCheckList()
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
            checkListId=""
            etCLTitle.setText("")

        }

        btnAddUpdate.setOnClickListener {

            checkListName = etCLTitle.text.toString()

            if (checkListName.isEmpty()) {
                Toast.makeText(context, "Enter Event Name", Toast.LENGTH_SHORT).show()
            }else {
                addUpdateCheckList()
            }
        }
    }

    private fun getCheckList() {

        slRecycle.startShimmerAnimation()
        slRecycle.visibility=View.VISIBLE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCheckList()

        call.enqueue(object : Callback<CheckListResponse> {
            override fun onResponse(
                call: Call<CheckListResponse>,
                response: Response<CheckListResponse>
            ) {
                if (response.body() != null) {
                    slRecycle.visibility=View.GONE
                    rvCheckList.visibility=View.VISIBLE
                    val checkListResponse = response.body() as CheckListResponse
                    arrOfCheckList = checkListResponse.arrOfCheckList
                    setEventAdapter()
                }
            }

            override fun onFailure(call: Call<CheckListResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setEventAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCheckList.layoutManager = layoutManager
        adminChecklistAdapter = AdminChecklistAdapter(context, arrOfCheckList, this)
        rvCheckList.adapter = adminChecklistAdapter
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
            .setMessage("Sure to delete checklist type  "+arrOfCheckList[position].checkedListTitle+" ?")
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteCheckList(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position=position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        checkListId = arrOfCheckList[position].checkedListId
        etCLTitle.setText(arrOfCheckList[position].checkedListTitle)
        btnAddUpdate.text = "Update"
    }

    private fun addUpdateCheckList() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateCheckList(
            userId, checkListId,checkListName)

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
                    if(checkListId==""){
                        val modelCheckList=ModelCheckList()
                        modelCheckList.checkedListId=responseStatusCode.id
                        modelCheckList.checkedListTitle=checkListName
                        arrOfCheckList.add(modelCheckList)
                        adminChecklistAdapter.notifyDataSetChanged()
                    }else{
                        arrOfCheckList[position].checkedListTitle=checkListName
                        adminChecklistAdapter.notifyDataSetChanged()
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


    private fun deleteCheckList(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteCheckList(arrOfCheckList[position].checkedListId)
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
                        arrOfCheckList.removeAt(position)
                        adminChecklistAdapter.notifyDataSetChanged()
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
