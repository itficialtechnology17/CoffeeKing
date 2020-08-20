package com.coffee.king.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.UpgradeEventAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.model.ModelUpgradeEvent
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UpgradeEventResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_upgrade_event.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UpgradeEventActivity : AppCompatActivity(), ItemClickCallback, TextWatcher {


    lateinit var context: Context
    var arrOfModelUpgradeEvent = ArrayList<ModelUpgradeEvent>()
    lateinit var upgradeEventAdapter: UpgradeEventAdapter

    var arrOfUpgradeEventId = ArrayList<String>()

    var eventId = ""

    private var totalCategoryPrice = 0
    var modelEventByDate = ModelEventByDate()
    private var advancePayment = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upgrade_event)
        supportActionBar!!.hide()
        context = this

        initDefine()
        initAction()
        getUpgradeEventList()
    }

    private fun initDefine() {

        if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
            modelEventByDate =
                intent.getSerializableExtra(ConstantKey.KEY_ARRAY) as ModelEventByDate
            eventId = modelEventByDate.bookingId

            for (i in 0 until modelEventByDate.arrOfPackage.size) {
                totalCategoryPrice += modelEventByDate.arrOfPackage[i].categoryPrice.toInt() * modelEventByDate.noOfPeople.toInt()
            }

            for (i in 0 until modelEventByDate.arrOfUpgradeEvent.size) {
                totalCategoryPrice += modelEventByDate.arrOfUpgradeEvent[i].upgradeEventPrice.toInt()
            }
            tvPackagePrice.text = "Rs. $totalCategoryPrice"
            etAdvancePayment.setText(modelEventByDate.advancePaidAmount)
            btnNext.text = "Update"

        } else {
            totalCategoryPrice = intent.getStringExtra(ConstantKey.KEY_STRING).toInt()
            tvPackagePrice.text = "Rs. $totalCategoryPrice"
            eventId = intent.getStringExtra(ConstantKey.KEY_ID)!!
            btnNext.text = "Finish"
        }

    }

    private fun initAction() {

        ivBack.setOnClickListener {
            finish()
        }

        etAdvancePayment.addTextChangedListener(this)
        btnNext.setOnClickListener {
            if (etAdvancePayment.text.isNotEmpty()) {
                advancePayment = etAdvancePayment.text.toString().toInt()
                if (advancePayment.toFloat() > totalCategoryPrice) {
                    Toast.makeText(context, "Please enter valid amount", Toast.LENGTH_SHORT).show()
                } else {
                    btnNext.visibility = View.GONE
                    rlLoader.visibility = View.VISIBLE
                    getUpgradeEventId()

                    if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                        updateUpgradeEvent(advancePayment)
                    } else {
                        addUpgradeEvent(advancePayment)
                    }
                }
            } else {
                Toast.makeText(context, "Please enter advance amount", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun afterTextChanged(s: Editable?) {
        if (s.toString() != "") {
            val enterPrice: Float = s.toString().toFloat()
            if (enterPrice > totalCategoryPrice) {
                Toast.makeText(context, "Please enter valid amount", Toast.LENGTH_SHORT).show()
                etAdvancePayment.setText("")
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    private fun getUpgradeEventId() {
        arrOfUpgradeEventId.clear()
        for (i in 0 until arrOfModelUpgradeEvent.size) {
            if (arrOfModelUpgradeEvent[i].isSelected) {
                arrOfUpgradeEventId.add(arrOfModelUpgradeEvent[i].upgradeEventId)
            }
        }
    }


    private fun getUpgradeEventList() {

        progressUpgradeEvent.visibility = View.VISIBLE
        rvUpgradeEvent.visibility = View.GONE
        btnNext.isClickable = false


        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUpgradeEventList()

        call.enqueue(object : Callback<UpgradeEventResponse> {
            override fun onResponse(
                call: Call<UpgradeEventResponse>,
                response: Response<UpgradeEventResponse>
            ) {

                progressUpgradeEvent.visibility = View.GONE
                rvUpgradeEvent.visibility = View.VISIBLE
                btnNext.isClickable = true

                if (response.body() != null) {
                    val upgradeEventResponse = response.body() as UpgradeEventResponse
                    arrOfModelUpgradeEvent = upgradeEventResponse.arrOfUpgradeEvent
                    if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                        setUpdateDetails()
                    } else {
                        setUpgradeEventAdapter()
                    }

                }
            }

            override fun onFailure(call: Call<UpgradeEventResponse>, t: Throwable) {
                progressUpgradeEvent.visibility = View.GONE
                rvUpgradeEvent.visibility = View.VISIBLE
                btnNext.isClickable = false
                t.printStackTrace()
            }
        })
    }

    private fun setUpdateDetails() {
        for (i in 0 until arrOfModelUpgradeEvent.size) {
            for (j in 0 until modelEventByDate.arrOfUpgradeEvent.size) {
                if (modelEventByDate.arrOfUpgradeEvent[j].upgradeEventId == arrOfModelUpgradeEvent[i].upgradeEventId) {
                    arrOfModelUpgradeEvent[i].isSelected = true
                }
            }
        }

        setUpgradeEventAdapter()
    }

    private fun setUpgradeEventAdapter() {
        viewEventType.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUpgradeEvent.layoutManager = layoutManager
        upgradeEventAdapter = UpgradeEventAdapter(context, arrOfModelUpgradeEvent, this, 1)
        rvUpgradeEvent.adapter = upgradeEventAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.tvAddRemove) {
            if (!arrOfModelUpgradeEvent[position].isSelected) {
                arrOfModelUpgradeEvent[position].isSelected = true
                totalCategoryPrice =
                    (totalCategoryPrice + arrOfModelUpgradeEvent[position].upgradeEventPrice.toInt())
            } else {
                arrOfModelUpgradeEvent[position].isSelected = false
                totalCategoryPrice =
                    (totalCategoryPrice - arrOfModelUpgradeEvent[position].upgradeEventPrice.toInt())
            }
            tvPackagePrice.text = "Rs. $totalCategoryPrice"
            upgradeEventAdapter.notifyDataSetChanged()
        }
    }

    private fun addUpgradeEvent(advancePayment: Int) {

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE


        val selectionJson = Gson().toJson(arrOfUpgradeEventId)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addUpgradeEvent(
                eventId,
                selectionJson,
                totalCategoryPrice.toString(),
                advancePayment
            )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    //Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (responseStatusCode.statusCode == 101) {
                        generatePdf()
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


    private fun updateUpgradeEvent(advancePayment: Int) {

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE


        val selectionJson = Gson().toJson(arrOfUpgradeEventId)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.updateUpgradeEvent(
                eventId,
                selectionJson,
                totalCategoryPrice.toString(),
                advancePayment
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
                        modelEventByDate.totalAmount = totalCategoryPrice.toString()
                        val arrOfUpgradeEvent = ArrayList<ModelUpgradeEvent>()
                        for (i in 0 until arrOfModelUpgradeEvent.size) {
                            if (arrOfModelUpgradeEvent[i].isSelected) {
                                arrOfUpgradeEvent.add(arrOfModelUpgradeEvent[i])
                            }
                        }
                        modelEventByDate.arrOfUpgradeEvent = arrOfUpgradeEvent
                        val intent = Intent()
                        intent.putExtra(ConstantKey.KEY_ARRAY, modelEventByDate)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
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

    private fun generatePdf() {


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
