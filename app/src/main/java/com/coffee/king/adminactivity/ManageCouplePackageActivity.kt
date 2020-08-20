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
import com.coffee.king.adminadapter.AdminCouplePackageAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.responseCallback.CouplePackageResponse
import com.coffee.king.responseCallback.ResponseStatusCode
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
import kotlinx.android.synthetic.main.activity_manage_couple_package.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCouplePackageActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfCouplePackage = ArrayList<ModelCouplePackage>()
    lateinit var adminCouplePackageAdapter: AdminCouplePackageAdapter

    var position = 0
    var couplePackageId = ""
    var packageTitle = ""
    var packagePrice = ""
    var packageType = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_couple_package)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if (Utils.isOnline(context)) {
            getCouplePackage()
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
            etEventTitle.setText("")
            etPrice.setText("")

            couplePackageId = ""
        }

        btnAddUpdate.setOnClickListener {

            packageTitle = etEventTitle.text.toString()
            packagePrice = etPrice.text.toString()


            if (packageTitle.isEmpty()) {
                Toast.makeText(context, "Enter Title", Toast.LENGTH_SHORT).show()
            } else if (packagePrice.isEmpty()) {
                Toast.makeText(context, "Enter Price", Toast.LENGTH_SHORT).show()
            } else if(packageType.isEmpty()){
                Toast.makeText(context, "Select Booking Venue", Toast.LENGTH_SHORT).show()
            }else{
                addUpdateCouplePackage()
            }
        }

        rgBookingVenue.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rbHall) {
                packageType = "1"
            } else if (checkedId == R.id.rbTable) {
                packageType = "2"
            }
        }
    }


    private fun getCouplePackage() {

        slRecycle.startShimmerAnimation()
        slRecycle.visibility = View.VISIBLE


        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCouplePackage()

        call.enqueue(object : Callback<CouplePackageResponse> {
            override fun onResponse(
                call: Call<CouplePackageResponse>,
                response: Response<CouplePackageResponse>
            ) {

                if (response.body() != null) {
                    slRecycle.startShimmerAnimation()
                    slRecycle.visibility = View.GONE
                    rvCouplePackage.visibility = View.VISIBLE

                    val couplePackageResponse = response.body() as CouplePackageResponse
                    arrOfCouplePackage = couplePackageResponse.arrOfCouplePackage
                    setAdapter()
                }
            }

            override fun onFailure(call: Call<CouplePackageResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCouplePackage.layoutManager = layoutManager
        adminCouplePackageAdapter =
            AdminCouplePackageAdapter(
                context,
                arrOfCouplePackage,
                this
            )
        rvCouplePackage.adapter = adminCouplePackageAdapter
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
            .setMessage("Sure to delete " + arrOfCouplePackage[position].packageTitle + "?")
            .setNegativeButton(android.R.string.no) { dialog, i ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteCouplePackage(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position = position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        couplePackageId = arrOfCouplePackage[position].couplePackageId
        etEventTitle.setText(arrOfCouplePackage[position].packageTitle)
        etPrice.setText(arrOfCouplePackage[position].packagePrice)

        if(arrOfCouplePackage[position].packageType=="1"){
            rbHall.isChecked=true
            rbTable.isChecked=false
            packageType="1"
        }else{
            rbHall.isChecked=false
            rbTable.isChecked=true
            packageType="2"
        }

        btnAddUpdate.text = "Update"
    }

    private fun addUpdateCouplePackage() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateCouplePackage(
            userId, couplePackageId, packageTitle, packagePrice,packageType)

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
                    if (couplePackageId == "") {
                        val modelCouplePackage = ModelCouplePackage()
                        modelCouplePackage.couplePackageId = responseStatusCode.id
                        modelCouplePackage.packageTitle = packageTitle
                        modelCouplePackage.packagePrice = packagePrice

                        arrOfCouplePackage.add(modelCouplePackage)
                        adminCouplePackageAdapter.notifyDataSetChanged()
                    } else {
                        arrOfCouplePackage[position].packageTitle = packageTitle
                        arrOfCouplePackage[position].packagePrice = packagePrice

                        adminCouplePackageAdapter.notifyDataSetChanged()
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


    private fun deleteCouplePackage(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteCouplePackage(arrOfCouplePackage[position].couplePackageId)
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
                        arrOfCouplePackage.removeAt(position)
                        adminCouplePackageAdapter.notifyDataSetChanged()
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
