package com.coffee.king.activity

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.FileProvider
import com.coffee.king.R
import com.kotharirefrigeration.constant.ConstantKey
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter
import kotlinx.android.synthetic.main.activity_pdf.*
import java.io.File

class PdfActivity : AppCompatActivity() {

    lateinit var context: Context
    lateinit var pdfPagerAdapter: PDFPagerAdapter
    lateinit var file:File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf)
        supportActionBar!!.hide()
        context=this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        file= File(intent.getStringExtra(ConstantKey.KEY_STRING)!!)
        pdfPagerAdapter = PDFPagerAdapter(context, file.absolutePath)
        viewpager.adapter = pdfPagerAdapter
    }

    private fun initAction() {
        ivBack.setOnClickListener {
            finish()
        }

        ivShare.setOnClickListener {
            val uri = FileProvider.getUriForFile(context, "$packageName.provider", file)
            sharePdf(uri)
        }
    }

    private fun sharePdf(uri: Uri) {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "image/*"
        val link = "https://play.google.com/store/apps/details?id=" + context.packageName
        share.putExtra(Intent.EXTRA_TEXT, link)
        share.putExtra(Intent.EXTRA_TITLE, link)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(share)
    }
}
