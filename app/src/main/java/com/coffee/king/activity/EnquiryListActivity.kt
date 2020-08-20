package com.coffee.king.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.BookEventAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelEventByDate
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_book_event_list.*
import java.text.SimpleDateFormat


class EnquiryListActivity : AppCompatActivity(), ItemClickCallback, TextWatcher {


    lateinit var context: Context
    var arrOfBookEvent = ArrayList<ModelEventByDate>()
    var arrOfFilterBookEvent = ArrayList<ModelEventByDate>()
    lateinit var bookEventAdapter: BookEventAdapter
    lateinit var colorRandom: IntArray
    var dateOfEvent = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enquiry_event_list)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        checkDate()
    }

    private fun checkDate() {
        val currentDate = Utils.getCurrentDate()
        val selectedDate = Utils.getDateFromString(intent.getStringExtra(ConstantKey.KEY_STRING)!!)

        val sdf = SimpleDateFormat("dd-MMM-yyyy")
        val date1 = sdf.parse(currentDate)
        val date2 = sdf.parse(selectedDate)

        /*if (date1 == date2) {
            //Equal
            ivAdd.visibility = View.VISIBLE
        }
        if (date1.before(date2)) {
            //Before Date
            ivAdd.visibility = View.VISIBLE
        }
        if (date1.after(date2)) {
            //After Date
            ivAdd.visibility = View.GONE
        }*/
    }

    private fun initDefine() {

        colorRandom = context.resources.getIntArray(R.array.colorBG)
        arrOfBookEvent =
            intent.getSerializableExtra(ConstantKey.KEY_ARRAY) as ArrayList<ModelEventByDate>
        dateOfEvent = intent.getStringExtra(ConstantKey.KEY_STRING)
        setAdapter()
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvBookEventList.layoutManager = layoutManager
        bookEventAdapter = BookEventAdapter(context, arrOfBookEvent, colorRandom, this)
        rvBookEventList.adapter = bookEventAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        var modelEventByDate: ModelEventByDate = if (arrOfFilterBookEvent.isEmpty()) {
            arrOfBookEvent[position]
        } else {
            arrOfFilterBookEvent[position]
        }
        startActivity(
            Intent(
                this,
                BookEventDetailsActivity::class.java
            ).putExtra(ConstantKey.KEY_ARRAY, modelEventByDate)
        )
    }

    private fun initAction() {
        ivBack.setOnClickListener {
            finish()
        }

        etSearch.addTextChangedListener(this)

        ivAdd.setOnClickListener {
            startActivity(
                Intent(
                    context,
                    BookingActivity::class.java
                ).putExtra(ConstantKey.KEY_STRING, dateOfEvent)
            )
        }
    }

    override fun afterTextChanged(p0: Editable?) {

    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {
        if (text!!.isNotEmpty()) {
            arrOfFilterBookEvent.clear()
            for (i in 0 until arrOfBookEvent.size) {
                if (arrOfBookEvent[i].customerName.toLowerCase().contains(text.toString().toLowerCase()) ||
                    arrOfBookEvent[i].customerNumber.toLowerCase().contains(text.toString().toLowerCase())
                ) {
                    arrOfFilterBookEvent.add(arrOfBookEvent[i])
                }
            }
            bookEventAdapter = BookEventAdapter(context, arrOfFilterBookEvent, colorRandom, this)
            rvBookEventList.adapter = bookEventAdapter
        } else {
            arrOfFilterBookEvent.clear()
            bookEventAdapter = BookEventAdapter(context, arrOfBookEvent, colorRandom, this)
            rvBookEventList.adapter = bookEventAdapter
        }
    }

}
