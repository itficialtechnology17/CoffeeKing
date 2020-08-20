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
import androidx.viewpager.widget.ViewPager
import com.coffee.king.R
import com.coffee.king.adapter.CategoryAdapter
import com.coffee.king.adapter.CouplePackageAdapter
import com.coffee.king.adapter.UpgradeEventAdapter
import com.coffee.king.adapter.ViewPagerAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.clickcallback.NestedItemClickCallback
import com.coffee.king.fragments.AddOnFragment
import com.coffee.king.fragments.DetailsFragment
import com.coffee.king.fragments.PackageFragment
import com.coffee.king.fragments.SummaryFragment
import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.model.ModelPackage
import com.coffee.king.model.ModelUpgradeEvent
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.downloader.Error
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import com.kotharirefrigeration.constant.ConstantValue
import kotlinx.android.synthetic.main.activity_book_event_details.*
import kotlinx.android.synthetic.main.activity_book_event_list.ivBack
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class BookEventDetailsActivity : AppCompatActivity(),
    ViewPager.OnPageChangeListener {



    lateinit var context: Context
    var modelEventByDate = ModelEventByDate()
    lateinit var viewPagerAdapter:ViewPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_event_details)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        setDetails()
    }

    private fun setDetails() {


        viewPager.addOnPageChangeListener(this)

        viewPagerAdapter = ViewPagerAdapter(this, supportFragmentManager)

        viewPagerAdapter.addFrag(DetailsFragment(modelEventByDate))
        viewPagerAdapter.addFrag(PackageFragment(modelEventByDate))
        viewPagerAdapter.addFrag(AddOnFragment(modelEventByDate))
        viewPagerAdapter.addFrag(SummaryFragment(modelEventByDate))

        viewPager.adapter = viewPagerAdapter
        viewPager.offscreenPageLimit = 4
    }

    private fun initDefine() {
        modelEventByDate = intent.getSerializableExtra(ConstantKey.KEY_ARRAY) as ModelEventByDate
    }

    private fun initAction() {
        ivBack.setOnClickListener {
            finish()
        }

        ivEdit.setOnClickListener {
            when {
                viewPager.currentItem==0 -> startActivityForResult(
                    Intent(this, BookingActivity::class.java).putExtra(
                        ConstantKey.KEY_ARRAY,
                        modelEventByDate
                    ), ConstantValue.REQUEST_CODE_DETAILS
                )
                viewPager.currentItem==1 ->
                    if (modelEventByDate.bookingType.toInt() == 1) {
                        startActivityForResult(
                            Intent(this, PackageActivity::class.java).putExtra(
                                ConstantKey.KEY_ARRAY,
                                modelEventByDate
                            ), ConstantValue.REQUEST_CODE_DETAILS
                        )
                    }
                viewPager.currentItem==2 -> if (modelEventByDate.bookingType.toInt() == 1) {
                    startActivityForResult(
                        Intent(this, UpgradeEventActivity::class.java).putExtra(
                            ConstantKey.KEY_ARRAY,
                            modelEventByDate
                        ), ConstantValue.REQUEST_CODE_DETAILS
                    )
                } else {
                    startActivityForResult(
                        Intent(this, CouplePackageActivity::class.java).putExtra(
                            ConstantKey.KEY_ARRAY,
                            modelEventByDate
                        ), ConstantValue.REQUEST_CODE_DETAILS
                    )
                }
            }
        }

        rlOne.setOnClickListener {
            changeColorOnClick(it)
        }

        rlTwo.setOnClickListener {
            changeColorOnClick(it)
        }

        rlThree.setOnClickListener {
            changeColorOnClick(it)
        }

        rlFour.setOnClickListener {
            changeColorOnClick(it)
        }

        btnNext.setOnClickListener {
            generatePdf()
        }


    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if(position==0){
            changeColorOnClick(rlOne)
        }else if(position==1){
            changeColorOnClick(rlTwo)
        }else if(position==2){
            changeColorOnClick(rlThree)
        }else if(position==3){
            changeColorOnClick(rlFour)
        }

        if(position==3){
            rlBottom.visibility=View.VISIBLE
        }else{
            rlBottom.visibility=View.GONE
        }
    }

    private fun changeColorOnClick(view: View) {

        when {
            view.id == R.id.rlOne -> {
                tvOne.setBackgroundResource(R.drawable.ic_circle_bg_theme)
                tvTwo.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvThree.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvFour.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                ivEdit.visibility=View.VISIBLE
                viewPager.currentItem=0
            }
            view.id == R.id.rlTwo -> {
                tvOne.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvTwo.setBackgroundResource(R.drawable.ic_circle_bg_theme)
                tvThree.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvFour.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                if(modelEventByDate.bookingType=="1"){
                    ivEdit.visibility=View.VISIBLE
                }else{
                    ivEdit.visibility=View.GONE
                }
                viewPager.currentItem=1
            }
            view.id == R.id.rlThree -> {
                tvOne.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvTwo.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvThree.setBackgroundResource(R.drawable.ic_circle_bg_theme)
                tvFour.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                ivEdit.visibility=View.VISIBLE
                viewPager.currentItem=2
            }
            view.id == R.id.rlFour -> {
                tvOne.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvTwo.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvThree.setBackgroundResource(R.drawable.ic_circle_bg_theme_light)
                tvFour.setBackgroundResource(R.drawable.ic_circle_bg_theme)
                ivEdit.visibility=View.GONE
                viewPager.currentItem=3
            }
        }
    }

    private fun generatePdf() {

        rlLoader.visibility = View.VISIBLE
        btnNext.visibility = View.GONE


//        val bookingDetails = Gson().toJson(modelEventByDate)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.generatePdf(modelEventByDate.bookingId)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {

                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    if(responseStatusCode.statusCode==101){
                        downloadPdf(responseStatusCode.message)
                    }else{
                        Toast.makeText(context,responseStatusCode.message,Toast.LENGTH_SHORT).show()
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
                }

                override fun onError(error: Error?) {
                    rlLoader.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    Toast.makeText(context, "Something Wrong", Toast.LENGTH_SHORT).show()
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ConstantValue.REQUEST_CODE_DETAILS && resultCode== Activity.RESULT_OK) {
            modelEventByDate =
                data!!.getSerializableExtra(ConstantKey.KEY_ARRAY) as ModelEventByDate
            changeColorOnClick(rlOne)
            setDetails()
        }
    }

}
