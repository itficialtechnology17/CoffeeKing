package com.coffee.king.staff

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
import kotlinx.android.synthetic.main.activity_my_schedule.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MyScheduleActivity : AppCompatActivity() {

    lateinit var context:Context
    var startDate=""
    var endDate=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_schedule)
        supportActionBar!!.hide()
        context=this
        initDefine()
        initAction()
    }

    private fun initDefine() {

    }

    private fun initAction() {

        ivBack.setOnClickListener { finish() }

        calendarShiftDate.setOnRangeSelectedListener { widget, dates ->
//            calendarShiftDate.visibility = View.GONE
            startDate =
                dates[0].day.toString() + "-" + dates[0].month.toString() + "-" + dates[0].year
            endDate =
                dates[dates.size - 1].day.toString() + "-" + dates[dates.size - 1].month.toString() + "-" + dates[dates.size - 1].year

            tvDateOfShift.text = "$startDate To $endDate"
        }

        btnSubmit.setOnClickListener {
            when {
                startDate.isEmpty() -> Toast.makeText(context,"Please select Start date",Toast.LENGTH_SHORT).show()
                endDate.isEmpty() -> Toast.makeText(context,"Please select End date",Toast.LENGTH_SHORT).show()
                else -> getMySchedule()
            }
        }
    }

    private fun getMySchedule() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId=Utils.getPref(context,ConstantKey.KEY_LOGIN_ID,"")!!

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getMySchedule(userId,startDate,endDate)

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
                btnSubmit.visibility = View.VISIBLE
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
            "MySchedule" + ".pdf"
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
                        File("$dirPath/MySchedule.pdf")
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
