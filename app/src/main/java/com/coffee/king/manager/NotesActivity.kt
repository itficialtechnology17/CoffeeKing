package com.coffee.king.manager

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.NotesAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelNotes
import com.coffee.king.responseCallback.NotesResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_notes.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NotesActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfNotes = ArrayList<ModelNotes>()
    lateinit var notesAdapter: NotesAdapter

    var notesId = ""
    var title = ""
    var noteDesc = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        getNotes()
    }

    private fun initAction() {

        ivBack.setOnClickListener { finish() }

        btnAdd.setOnClickListener {
            rlList.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
        }

        btnSubmit.setOnClickListener {
            title = etTitle.text.toString()
            noteDesc = etDesc.text.toString()

            if (Utils.isOnline(context)) {
                when {
                    title == "" -> Toast.makeText(
                        context,
                        "Please enter Title",
                        Toast.LENGTH_SHORT
                    ).show()

                    noteDesc == "" -> Toast.makeText(
                        context,
                        "Your Description Is Empty",
                        Toast.LENGTH_SHORT
                    ).show()

                    else ->
                        if (notesId == "" ) {
                            addNotes()
                        } else {
                            updateNotes()
                        }
                }
            } else {
                Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onBackPressed() {
        if (rlContent.visibility == View.VISIBLE) {
            rlList.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

    private fun addNotes() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addNotes(userId, title, noteDesc)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    rlList.visibility = View.VISIBLE
                    rlContent.visibility = View.GONE
                    getNotes()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun updateNotes() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.updateNotes(userId, notesId, title, noteDesc)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    rlList.visibility = View.VISIBLE
                    rlContent.visibility = View.GONE
                    btnSubmit.text = "Submit"
                    notesId=""
                    getNotes()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun getNotes() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getNotes(userId)

        call.enqueue(object : Callback<NotesResponse> {
            override fun onResponse(
                call: Call<NotesResponse>,
                response: Response<NotesResponse>
            ) {
                if (response.body() != null) {
                    val notesResponse = response.body() as NotesResponse
                    arrOfNotes = notesResponse.arrOfNotes
                    setNotesAdapter()
                }
            }

            override fun onFailure(call: Call<NotesResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setNotesAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvNotes.layoutManager = layoutManager
        notesAdapter = NotesAdapter(context, arrOfNotes, this)
        rvNotes.adapter = notesAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.rlContentBG) {
            rlList.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
            title = arrOfNotes[position].noteTitle
            noteDesc = arrOfNotes[position].noteDesc
            etTitle.setText(title)
            etDesc.setText(noteDesc)
            notesId = arrOfNotes[position].noteId

            btnSubmit.text = "Update"
        }
    }
}
