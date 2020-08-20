package com.coffee.king.manager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.activity.PdfActivity
import com.coffee.king.adapter.InChargeAdapter
import com.coffee.king.adapter.SelectedDateAdapter
import com.coffee.king.adapter.ShiftAdapter
import com.coffee.king.adapter.ShiftDateAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelDate
import com.coffee.king.model.ModelShift
import com.coffee.king.model.ModelShiftDate
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.ShiftResponse
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_employee_deployment.*
import kotlinx.android.synthetic.main.dialog_pdf_generate.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class EmployeeDeploymentActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfUser = ArrayList<ModelUser>()
    var arrOfShift = ArrayList<ModelShift>()
    var arrOfShiftDate = ArrayList<ModelShiftDate>()
    private lateinit var userAdapter: InChargeAdapter
    lateinit var shiftAdapter: ShiftAdapter
    private lateinit var selectedDateAdapter: SelectedDateAdapter
    lateinit var shiftDateAdapter: ShiftDateAdapter
    var arrOfDate = ArrayList<ModelDate>()
    private var employeeId = ""
    var shiftId = ""
    private var selectedDate = ""
    var employeeDeploymentID = ""
    var noOfSelectedDate = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee_deployment)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        getUser()
        getShift()
    }

    private fun initAction() {

        ivBack.setOnClickListener { finish() }

        ivPdf.setOnClickListener { openPdfView() }

        rlInChargeUser.setOnClickListener {
            if (arrOfUser.isEmpty()) {
                getUser()
            }
            if (progressInCharge.visibility != View.VISIBLE) {
                if (viewInChargeUser.visibility == View.GONE) {
                    viewInChargeUser.visibility = View.VISIBLE
                    ivIArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewInChargeUser.visibility = View.GONE
                    ivIArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }

        rlShift.setOnClickListener {
            if (arrOfShift.isEmpty()) {
                getShift()
            }
            if (progressShift.visibility != View.VISIBLE) {
                if (viewShift.visibility == View.GONE) {
                    viewShift.visibility = View.VISIBLE
                    ivSArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewShift.visibility = View.GONE
                    ivSArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }

        llDateOfShift.setOnClickListener {
            if (calendarShiftDate.visibility == View.GONE) {
                calendarShiftDate.visibility = View.VISIBLE
            } else {
                calendarShiftDate.visibility = View.GONE
            }
        }

        calendarShiftDate.setOnRangeSelectedListener { widget, dates ->
            calendarShiftDate.visibility = View.GONE

            noOfSelectedDate=dates.size

            arrOfShiftDate.clear()
            val startDate =
                dates[0].day.toString() + "-" + dates[0].month.toString() + "-" + dates[0].year
            val endDate =
                dates[dates.size - 1].day.toString() + "-" + dates[dates.size - 1].month.toString() + "-" + dates[dates.size - 1].year

            tvDateOfShift.text = "$startDate To $endDate"
            arrOfDate = ArrayList()
            for (i in 0 until dates.size) {
                val modelDate = ModelDate()
                modelDate.dateDay = dates[i].day.toString()
                var month=""
                month = if(dates[i].month.toString().length==1){
                    "0"+dates[i].month.toString()
                }else{
                    dates[i].month.toString()
                }
                modelDate.date=dates[i].year.toString()+ "-" +month+"-"+dates[i].day.toString()
                arrOfDate.add(modelDate)
            }
            setDateAdapter()
            setShiftDateAdapter()
            llShift.visibility = View.VISIBLE
        }

        btnSubmit.setOnClickListener {
            if (employeeDeploymentID.isEmpty()) {
                when {
                    employeeId.isEmpty() -> Toast.makeText(
                        context,
                        "Please Select Employee",
                        Toast.LENGTH_SHORT
                    ).show()

                    arrOfShiftDate.isEmpty() || noOfSelectedDate == 0 ->
                        Toast.makeText(
                            context,
                            "Please Set Shift Schedule",
                            Toast.LENGTH_SHORT
                        ).show()

                    noOfSelectedDate != arrOfShiftDate.size ->
                        Toast.makeText(
                            context,
                            "Some Of Date Schedule Is Missing",
                            Toast.LENGTH_SHORT
                        ).show()

                    else -> addShift()

                }
            }
        }
    }


    private fun addShift() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val addedBy=Utils.getPref(context,ConstantKey.KEY_LOGIN_ID,"")!!

        val selectionJson = Gson().toJson(arrOfShiftDate)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addShift(addedBy, employeeId, selectionJson)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    rlLoader.visibility = View.GONE
                    btnSubmit.visibility = View.VISIBLE
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    arrOfShiftDate.clear()
                    arrOfDate.clear()
                    tvDateOfShift.text="Date Of Shift"
                    selectedDateAdapter.notifyDataSetChanged()
                    shiftDateAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setDateAdapter() {
        rvSelectedDate.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
        rvSelectedDate.layoutManager = layoutManager
        selectedDateAdapter = SelectedDateAdapter(context, arrOfDate, this)
        rvSelectedDate.adapter = selectedDateAdapter
    }

    private fun setShiftDateAdapter() {
        rvShiftDate.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvShiftDate.layoutManager = layoutManager
        shiftDateAdapter = ShiftDateAdapter(context, arrOfShiftDate, this)
        rvShiftDate.adapter = shiftDateAdapter
    }

    private fun getUser() {

        progressInCharge.visibility = View.VISIBLE
        ivIArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUser()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                progressInCharge.visibility = View.GONE
                ivIArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    setUserAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progressInCharge.visibility = View.GONE
                ivIArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }


    private fun setUserAdapter() {
        rvUser.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUser.layoutManager = layoutManager
        userAdapter = InChargeAdapter(context, arrOfUser, this)
        rvUser.adapter = userAdapter
    }

    private fun openPdfView(){
        var isDateSelected=false
        var startDate=""
        var endDate=""

        val alert = AlertDialog.Builder(context)
        val alertLayout = LayoutInflater.from(context).inflate(R.layout.dialog_pdf_generate, null)
        alert.setView(alertLayout)
        alert.setCancelable(true)
        val dialog = alert.create()
        dialog.show()

        alertLayout.calendarDialogDate.setOnRangeSelectedListener { widget, dates ->
            isDateSelected=true
            var startMonth=""
            startMonth = if(dates[0].month.toString().length==1){
                "0"+dates[0].month.toString()
            }else{
                dates[0].month.toString()
            }

            startDate=dates[0].year.toString()+ "-" +startMonth+"-"+dates[0].day.toString()

            var endMonth=""
            endMonth = if(dates[dates.size-1].month.toString().length==1){
                "0"+dates[dates.size-1].month.toString()
            }else{
                dates[dates.size-1].month.toString()
            }

            endDate=dates[dates.size-1].year.toString()+ "-" +endMonth+"-"+dates[dates.size-1].day.toString()
        }

        alertLayout.tvView.setOnClickListener {
            if(!isDateSelected){
                Toast.makeText(context,"Please select date in range",Toast.LENGTH_SHORT).show()
            }else{
                progressPdf.visibility=View.VISIBLE
                ivPdf.visibility=View.GONE
                getPdf(startDate,endDate)
                dialog.dismiss()
            }
        }
        alertLayout.tvCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun getPdf(startDate: String, endDate: String) {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getPdf(startDate,endDate)

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
            "Test" + ".pdf"
        )
            .build()
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                    progressPdf.visibility = View.GONE
                    ivPdf.visibility=View.VISIBLE
                    Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show()
                }

                override fun onDownloadComplete() {
                    progressPdf.visibility = View.GONE
                    ivPdf.visibility=View.VISIBLE
                    val file =
                        File("$dirPath/Test.pdf")
                    startActivity(
                        Intent(
                            context,
                            PdfActivity::class.java
                        ).putExtra(ConstantKey.KEY_STRING, file.absolutePath)
                    )
                }
            })
    }

    private fun getShift() {

        progressShift.visibility = View.VISIBLE
        ivSArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getShift()

        call.enqueue(object : Callback<ShiftResponse> {
            override fun onResponse(
                call: Call<ShiftResponse>,
                response: Response<ShiftResponse>
            ) {
                progressShift.visibility = View.GONE
                ivSArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val shiftResponse = response.body() as ShiftResponse
                    arrOfShift = shiftResponse.arrOfShift
                    setShiftAdapter()
                }
            }

            override fun onFailure(call: Call<ShiftResponse>, t: Throwable) {
                progressShift.visibility = View.GONE
                ivSArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }


    private fun setShiftAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvShift.layoutManager = layoutManager
        shiftAdapter = ShiftAdapter(context, arrOfShift, this)
        rvShift.adapter = shiftAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        when {
            view.id == R.id.rlBGInCharge -> {
                if (!arrOfUser[position].isSelected) {
                    for (i in 0 until arrOfUser.size) {
                        arrOfUser[i].isSelected = false
                    }
                    tvInChargeUser.text = arrOfUser[position].userName
                    arrOfUser[position].isSelected = true
                    employeeId = arrOfUser[position].userId
                    viewInChargeUser.visibility = View.GONE
                    ivIArrow.setImageResource(R.drawable.ic_arrow_down)
                } else {
                    arrOfUser[position].isSelected = false
                    employeeId = ""
                    tvInChargeUser.text = ""
                }

                userAdapter.notifyDataSetChanged()
            }
            view.id == R.id.rlShift -> {
                if (selectedDate.isNotEmpty()) {
                    if (!arrOfShift[position].isSelected) {
                        for (i in 0 until arrOfShift.size) {
                            arrOfShift[i].isSelected = false
                        }

                        val modelShiftDate = ModelShiftDate()

                        modelShiftDate.shiftDate = selectedDate
                        modelShiftDate.shiftId = arrOfShift[position].shiftId
                        modelShiftDate.shiftName = arrOfShift[position].shiftName
                        modelShiftDate.shiftStartTime = arrOfShift[position].shiftStartTime
                        modelShiftDate.shiftEndTime = arrOfShift[position].shiftEndTime
                        for (i in 0 until arrOfShiftDate.size) {
                            if (arrOfShiftDate[i].shiftDate == selectedDate) {
                                arrOfShiftDate.removeAt(i)
                                break
                            }
                        }
                        arrOfShiftDate.add(modelShiftDate)



                        arrOfShiftDate.sortWith(Comparator { o1, o2 ->
                            Utils.getDateForCompare(o1.shiftDate)
                                .compareTo(Utils.getDateForCompare(o2.shiftDate))
                        })

                        shiftDateAdapter.notifyDataSetChanged()

                        for (i in 0 until arrOfShiftDate.size) {
                            arrOfShiftDate[i].isSelected = false
                        }
                        for (j in 0 until arrOfShiftDate.size) {
                            if (arrOfShiftDate[j].shiftDate == selectedDate) {
                                arrOfShiftDate[j].isSelected = true
                                break
                            }
                        }

                        viewShift.visibility = View.GONE
                        ivSArrow.setImageResource(com.coffee.king.R.drawable.ic_arrow_down)
                    } else {
                        arrOfShift[position].isSelected = false
                        shiftId = ""
                        tvShift.text = ""
                    }
                } else {
                    Toast.makeText(context, "Please Select Date First", Toast.LENGTH_SHORT).show()
                }
            }
            view.id == R.id.rlSelectedDate -> {
                if (!arrOfDate[position].isSelected) {
                    for (i in 0 until arrOfDate.size) {
                        arrOfDate[i].isSelected = false
                    }
                    arrOfDate[position].isSelected = true
                    selectedDate = arrOfDate[position].date
                    for (i in 0 until arrOfShiftDate.size) {
                        arrOfShiftDate[i].isSelected = false
                    }
                    for (j in 0 until arrOfShiftDate.size) {
                        if (arrOfShiftDate[j].shiftDate == selectedDate) {
                            arrOfShiftDate[j].isSelected = true
                            break
                        }
                    }
                    shiftDateAdapter.notifyDataSetChanged()
                    viewShift.visibility = View.VISIBLE
                    ivSArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    arrOfDate[position].isSelected = false
                }
                selectedDateAdapter.notifyDataSetChanged()
            }
        }
    }

}
