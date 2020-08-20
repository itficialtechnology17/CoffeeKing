package com.coffee.king.manager

import android.app.DatePickerDialog
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
import com.coffee.king.adapter.DutyAdapter
import com.coffee.king.adapter.InChargeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelDuty
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.DutyResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_duties_chart.*
import kotlinx.android.synthetic.main.dialog_duty_chart_pdf_.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DutiesChartActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfUser = ArrayList<ModelUser>()
    var arrOfDuty = ArrayList<ModelDuty>()
    var arrOfSelectedDuty = ArrayList<ModelDuty>()
    private lateinit var userAdapter: InChargeAdapter
    private lateinit var dutyAdapter: DutyAdapter
    var employeeId = ""
    lateinit var calendar: Calendar
    var startDate = ""
    var endDate = ""

    var yearSelected = 0
    var monthSelected = 0
    var monthName="";
    var isSelectFromDialog = false;
    lateinit var dialogFragment: MonthYearPickerDialogFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_duties_chart)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        setMonthYearPicker()
    }

    private fun setMonthYearPicker() {

        val calendar = Calendar.getInstance()
        yearSelected = calendar[Calendar.YEAR]
        monthSelected = calendar[Calendar.MONTH]

        dialogFragment = MonthYearPickerDialogFragment
            .getInstance(monthSelected, yearSelected, "Select Month & Year")

        dialogFragment.setOnDateSetListener { year, monthOfYear ->
            yearSelected = year
            monthSelected = monthOfYear+1
            monthName=Utils.getMonthNameFromNumber(monthOfYear)
            if (isSelectFromDialog) {
                tvMonthName.text = Utils.getMonthNameFromNumber(monthOfYear)
                tvYear.text = year.toString()
            }else{
                getDutyChartPdf()
            }
        }
    }

    private fun initDefine() {
        getUser()
        getDuties()
    }

    private fun initAction() {

        ivBack.setOnClickListener {
            finish()
        }

        calendar = Calendar.getInstance()

        /*  val dateStartListener =
              DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                  calendar.set(Calendar.YEAR, year)
                  calendar.set(Calendar.MONTH, monthOfYear)
                  calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                  val myFormat = "dd-MMM-yyyy"
                  val sdf = SimpleDateFormat(myFormat, Locale.US)
                  tvStartDate.text = sdf.format(calendar.time)
                  startDate = tvStartDate.text.toString()
              }*/


        /*val dateEndListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tvEndDate.text = sdf.format(calendar.time)
                endDate = tvEndDate.text.toString()
            }*/

        llMonth.setOnClickListener {
            isSelectFromDialog = true
            dialogFragment.show(supportFragmentManager, null);
        }

        llYear.setOnClickListener {
            isSelectFromDialog = true
            dialogFragment.show(supportFragmentManager, null);
        }

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

        btnSubmit.setOnClickListener {
            arrOfSelectedDuty.clear()
            for (i in 0 until arrOfDuty.size) {
                if (arrOfDuty[i].isSelected) {
                    arrOfSelectedDuty.add(arrOfDuty[i])
                }
            }

            when {
                employeeId.isEmpty() -> Toast.makeText(
                    context,
                    "Please Select Employee",
                    Toast.LENGTH_SHORT
                ).show()

                monthSelected == 0 ->
                    Toast.makeText(
                        context,
                        "Please Select Month",
                        Toast.LENGTH_SHORT
                    ).show()

                yearSelected == 0 ->
                    Toast.makeText(
                        context,
                        "Please Select Year",
                        Toast.LENGTH_SHORT
                    ).show()

                arrOfSelectedDuty.isEmpty() ->
                    Toast.makeText(
                        context,
                        "Please Select Duties",
                        Toast.LENGTH_SHORT
                    ).show()

                else -> addDutyChart()

            }
        }

        ivPdf.setOnClickListener {
//            openPdfView()
            isSelectFromDialog = false
            dialogFragment.show(supportFragmentManager, null);
        }
    }


    private fun openPdfView() {
        var pdfStartDate = ""
        var pdfEndDate = ""

        val alert = AlertDialog.Builder(context)
        val alertLayout =
            LayoutInflater.from(context).inflate(R.layout.dialog_duty_chart_pdf_, null)
        alert.setView(alertLayout)
        alert.setCancelable(true)
        val dialog = alert.create()
        dialog.show()

        val dateStartListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                alertLayout.tvStartDate.text = sdf.format(calendar.time)
                pdfStartDate = alertLayout.tvStartDate.text.toString()
            }


        val dateEndListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                alertLayout.tvEndDate.text = sdf.format(calendar.time)
                pdfEndDate = alertLayout.tvEndDate.text.toString()
            }


        alertLayout.llStartDate.setOnClickListener {
            DatePickerDialog(
                this, dateStartListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        alertLayout.llEndDate.setOnClickListener {
            DatePickerDialog(
                this, dateEndListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        alertLayout.tvView.setOnClickListener {
            if (pdfStartDate.isEmpty() && pdfEndDate.isEmpty()) {
                Toast.makeText(context, "Please Select Date ", Toast.LENGTH_SHORT).show()
            } else {
                progressPdf.visibility = View.VISIBLE
                ivPdf.visibility = View.GONE
//                getDutyChartPdf(pdfStartDate, pdfEndDate)
                dialog.dismiss()
            }
        }
        alertLayout.tvCancel.setOnClickListener { dialog.dismiss() }
    }

    private fun getDutyChartPdf() {

        progressPdf.visibility = View.VISIBLE
        ivPdf.visibility = View.GONE


        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getDutyChartPdf(monthSelected,yearSelected,monthName)

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
            "DutyChart" + ".pdf"
        )
            .build()
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                    progressPdf.visibility = View.GONE
                    ivPdf.visibility = View.VISIBLE
                    Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show()
                }

                override fun onDownloadComplete() {
                    progressPdf.visibility = View.GONE
                    ivPdf.visibility = View.VISIBLE
                    val file =
                        File("$dirPath/DutyChart.pdf")
                    startActivity(
                        Intent(
                            context,
                            PdfActivity::class.java
                        ).putExtra(ConstantKey.KEY_STRING, file.absolutePath)
                    )
                }
            })
    }

    private fun addDutyChart() {

        rlLoader.visibility = View.VISIBLE


        btnSubmit.visibility = View.GONE

        val addedBy = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!


        val selectionJson = Gson().toJson(arrOfSelectedDuty)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addDutyChart(
            addedBy,
            employeeId,
            monthSelected,
            yearSelected,
            selectionJson
        )

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
                    if (responseStatusCode.statusCode == 101) {
                        arrOfSelectedDuty.clear()
                        tvInChargeUser.text = ""
                        employeeId = ""
                        for (i in 0 until arrOfDuty.size) {
                            arrOfDuty[i].isSelected = false
                        }
                        dutyAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
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

    private fun getDuties() {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getDuties()

        call.enqueue(object : Callback<DutyResponse> {
            override fun onResponse(
                call: Call<DutyResponse>,
                response: Response<DutyResponse>
            ) {
                progressDuty.visibility = View.GONE
                if (response.body() != null) {
                    val dutyResponse = response.body() as DutyResponse
                    arrOfDuty = dutyResponse.arrOfDuty
                    setDutyAdapter()
                }
            }

            override fun onFailure(call: Call<DutyResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setDutyAdapter() {
        rvDuties.visibility = View.VISIBLE
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvDuties.layoutManager = layoutManager
        dutyAdapter = DutyAdapter(context, arrOfDuty, this)
        rvDuties.adapter = dutyAdapter
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

            view.id == R.id.rltBGDuty -> {
                arrOfDuty[position].isSelected = !arrOfDuty[position].isSelected

                dutyAdapter.notifyDataSetChanged()
            }
        }
    }
}
