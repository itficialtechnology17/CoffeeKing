package com.coffee.king.staff

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.coffee.king.R
import com.coffee.king.activity.PdfActivity
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_my_duty.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MyDutyActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var calendar: Calendar
    var startDate=""
    var endDate=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_duty)
        context=this
        supportActionBar!!.hide()
        initDefine()
        initAction()

    }

    private fun initDefine() {
        calendar = Calendar.getInstance()
    }

    private fun initAction() {

        ivBack.setOnClickListener { finish() }

        val dateStartListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tvStartDate.text = sdf.format(calendar.time)
                startDate = tvStartDate.text.toString()
            }


        val dateEndListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                val myFormat = "dd-MMM-yyyy"
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                tvEndDate.text = sdf.format(calendar.time)
                endDate = tvEndDate.text.toString()
            }

        llStartDate.setOnClickListener {
            DatePickerDialog(
                this, dateStartListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        llEndDate.setOnClickListener {
            DatePickerDialog(
                this, dateEndListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        btnSubmit.setOnClickListener {
            if(startDate.isEmpty()){
                Toast.makeText(context,"Please select Start date",Toast.LENGTH_SHORT).show()
            }else if(endDate.isEmpty()){
                Toast.makeText(context,"Please select End date",Toast.LENGTH_SHORT).show()
            }else{
                getMyDutyPdf()
            }
        }
    }


    private fun getMyDutyPdf() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId= Utils.getPref(context,ConstantKey.KEY_LOGIN_ID,"")!!

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getMyDutyPdf(userId,startDate,endDate)

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
            "MyDuty" + ".pdf"
        )
            .build()
            .start(object : OnDownloadListener {
                override fun onError(error: com.downloader.Error?) {
                    rlLoader.visibility = View.GONE
                    btnSubmit.visibility = View.VISIBLE
                    Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show()
                }

                override fun onDownloadComplete() {
                    rlLoader.visibility = View.GONE
                    btnSubmit.visibility = View.VISIBLE
                    val file =
                        File("$dirPath/MyDuty.pdf")
                    startActivity(
                        Intent(
                            context,
                            PdfActivity::class.java
                        ).putExtra(ConstantKey.KEY_STRING, file.absolutePath)
                    )
                }
            })
    }

}
