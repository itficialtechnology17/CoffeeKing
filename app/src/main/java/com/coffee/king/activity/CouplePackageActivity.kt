package com.coffee.king.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.CouplePackageAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.responseCallback.CouplePackageResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_couple_package.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class CouplePackageActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfCouplePackage = ArrayList<ModelCouplePackage>()
    lateinit var couplePackageAdapter: CouplePackageAdapter

    private var arrOfCouplePackageId = ArrayList<String>()

    var eventId = ""
    var venueType = 0
    var totalPrice = 0
    var advancePayment = 0

    var modelEventByDate = ModelEventByDate()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_couple_package)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {

        if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
            modelEventByDate =
                intent.getSerializableExtra(ConstantKey.KEY_ARRAY) as ModelEventByDate
            eventId = modelEventByDate.bookingId
            venueType = modelEventByDate.bookingVenue.toInt()

            advancePayment=modelEventByDate.advancePaidAmount.toInt()
            etAdvancePayment.setText(advancePayment.toString())
        } else {
            eventId = intent.getStringExtra(ConstantKey.KEY_ID)!!
            venueType = intent.getIntExtra(ConstantKey.KEY_BOOKING_VENUE, 0)
        }

        if (venueType == 1) {
            tvTitle.text = "Party At Hall"
        } else {
            tvTitle.text = "Party At Table"
        }
        getCouplePackage()
    }

    private fun initAction() {

        btnNext.setOnClickListener {

            if (etAdvancePayment.text.isNotEmpty()) {
                advancePayment = etAdvancePayment.text.toString().toInt()
                if (advancePayment.toFloat() > totalPrice) {
                    Toast.makeText(context, "Please enter valid amount", Toast.LENGTH_SHORT).show()
                } else {
                    btnNext.visibility = View.GONE
                    rlLoader.visibility = View.VISIBLE
                    getCouplePackageId()

                    if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                        updateCouplePackageMaster()
                    } else {
                        addCouplePackageMaster()
                    }
                }
            }
        }



        ivBack.setOnClickListener { finish() }
    }

    private fun getCouplePackageId() {
        arrOfCouplePackageId.clear()
        for (i in 0 until arrOfCouplePackage.size) {
            if (arrOfCouplePackage[i].isSelected) {
                arrOfCouplePackageId.add(arrOfCouplePackage[i].couplePackageId)
            }
        }
    }


    private fun getCouplePackage() {

        progress.visibility = View.VISIBLE
        rvTableHallAddedOn.visibility = View.GONE
        btnNext.isClickable = false


        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCouplePackage(venueType)

        call.enqueue(object : Callback<CouplePackageResponse> {
            override fun onResponse(
                call: Call<CouplePackageResponse>,
                response: Response<CouplePackageResponse>
            ) {

                progress.visibility = View.GONE
                rvTableHallAddedOn.visibility = View.VISIBLE
                btnNext.isClickable = true

                if (response.body() != null) {
                    val couplePackageResponse = response.body() as CouplePackageResponse
                    arrOfCouplePackage = couplePackageResponse.arrOfCouplePackage
                    if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                        setUpdateDetails()
                    } else {
                        setUpgradeEventAdapter()
                    }
                }
            }

            override fun onFailure(call: Call<CouplePackageResponse>, t: Throwable) {
                progress.visibility = View.GONE
                rvTableHallAddedOn.visibility = View.VISIBLE
                btnNext.isClickable = false
                t.printStackTrace()
            }
        })
    }

    private fun setUpdateDetails() {
        for(i in 0 until arrOfCouplePackage.size){
            for(j in 0 until modelEventByDate.arrOfCouplePackage.size){
                if(modelEventByDate.arrOfCouplePackage[j].couplePackageId==arrOfCouplePackage[i].couplePackageId){
                    arrOfCouplePackage[i].isSelected=true
                }
            }
        }
        totalPrice=modelEventByDate.totalAmount.toInt()
        tvPackagePrice.text = "Rs. $totalPrice"
        setUpgradeEventAdapter()
    }

    private fun setUpgradeEventAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvTableHallAddedOn.layoutManager = layoutManager
        couplePackageAdapter = CouplePackageAdapter(context, arrOfCouplePackage, this, 1)
        rvTableHallAddedOn.adapter = couplePackageAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.tvAddRemove) {
            if (!arrOfCouplePackage[position].isSelected) {
                arrOfCouplePackage[position].isSelected = true
                totalPrice = (totalPrice + arrOfCouplePackage[position].packagePrice.toInt())
            } else {
                arrOfCouplePackage[position].isSelected = false
                totalPrice = (totalPrice - arrOfCouplePackage[position].packagePrice.toInt())
            }
            tvPackagePrice.text = "Rs. $totalPrice"
            couplePackageAdapter.notifyDataSetChanged()
        }
    }

    private fun addCouplePackageMaster() {

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE


        val selectionJson = Gson().toJson(arrOfCouplePackageId)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addCouplePackageMaster(
            eventId, selectionJson,
            totalPrice.toString(), advancePayment
        )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
//                rlLoader.visibility = View.GONE
//                btnNext.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    if(responseStatusCode.statusCode==101){
                        generatePdf()
                    }else{
                        Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }



    private fun updateCouplePackageMaster() {

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE


        val selectionJson = Gson().toJson(arrOfCouplePackageId)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.updateCouplePackageMaster(
            eventId, selectionJson,
            totalPrice.toString(), advancePayment
        )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (responseStatusCode.statusCode == 101) {
                        modelEventByDate.advancePaidAmount = advancePayment.toString()
                        modelEventByDate.totalAmount = totalPrice.toString()
                        val arrOfUpdatedCouplePackage = ArrayList<ModelCouplePackage>()
                        for (i in 0 until arrOfCouplePackage.size) {
                            if (arrOfCouplePackage[i].isSelected) {
                                arrOfUpdatedCouplePackage.add(arrOfCouplePackage[i])
                            }
                        }
                        modelEventByDate.arrOfCouplePackage = arrOfUpdatedCouplePackage
                        val intent = Intent()
                        intent.putExtra(ConstantKey.KEY_ARRAY, modelEventByDate)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                    finish()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun generatePdf() {

        val bookingDetails = Gson().toJson(modelEventByDate)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.generatePdf(eventId)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {

                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    downloadPdf(responseStatusCode.message)
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    fun downloadPdf(message: String) {
        val dirPath =
            File(cacheDir.toString() + File.separator + context.resources.getString(R.string.app_name) + File.separator)
        if (!dirPath.exists()) {
            dirPath.mkdirs()
        }


        PRDownloader.download(
            message,
            dirPath.path,
            modelEventByDate.customerName + modelEventByDate.bookingId + ".pdf"
        )
            .build()
            .start(object : OnDownloadListener {

                override fun onDownloadComplete() {
                    rlLoader.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    val file =
                        File(dirPath.toString() + "/" + modelEventByDate.customerName + modelEventByDate.bookingId + ".pdf")
                    startActivity(
                        Intent(
                            context,
                            PdfActivity::class.java
                        ).putExtra(ConstantKey.KEY_STRING, file.absolutePath)
                    )
                    finish()
                }

                override fun onError(error: com.downloader.Error?) {
                    rlLoader.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show()
                }

            })
    }

}
