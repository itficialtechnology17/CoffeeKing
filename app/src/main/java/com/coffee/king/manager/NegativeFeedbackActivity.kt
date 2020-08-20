package com.coffee.king.manager

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adapter.NegativeFeedbackAdapter
import com.coffee.king.adminadapter.UserWithTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelNegativeFeedback
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.NegativeFeedbackResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_negative_feedback.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NegativeFeedbackActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context
    var arrOfUser = ArrayList<ModelUser>()
    var arrOfNegativeFeedback = ArrayList<ModelNegativeFeedback>()
    private lateinit var userWithTypeAdapter: UserWithTypeAdapter
    lateinit var negativeFeedbackAdapter: NegativeFeedbackAdapter

    var arrOfResponsibleName = ArrayList<String>()
    var arrOfResponsibleId = ArrayList<String>()
    var responsibleId = ""
    var issueComplaints = ""
    var solution = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_negative_feedback)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        getNegativeFeedback()
        getUser()
    }

    private fun initAction() {
        ivBack.setOnClickListener { finish() }

        btnAdd.setOnClickListener {
            rlList.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
        }

        rlUser.setOnClickListener {
            if (arrOfUser.isEmpty()) {
                getUser()
            }
            if (progress.visibility != View.VISIBLE) {
                if (viewUser.visibility == View.GONE) {
                    viewUser.visibility = View.VISIBLE
                    ivUArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewUser.visibility = View.GONE
                    ivUArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }

        btnSubmit.setOnClickListener {
            issueComplaints = etIssueComplaints.text.toString()
            solution = etSolution.text.toString()

            if (Utils.isOnline(context)) {
                when {
                    issueComplaints == "" -> Toast.makeText(
                        context,
                        "Please enter issue",
                        Toast.LENGTH_SHORT
                    ).show()

                    responsibleId == "" -> Toast.makeText(
                        context,
                        "Please select responsible person",
                        Toast.LENGTH_SHORT
                    ).show()

                    else -> addNegativeFeedback()
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

    private fun getNegativeFeedback() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getNegativeFeedback(userId)

        call.enqueue(object : Callback<NegativeFeedbackResponse> {
            override fun onResponse(
                call: Call<NegativeFeedbackResponse>,
                response: Response<NegativeFeedbackResponse>
            ) {
                if (response.body() != null) {
                    rvUser.visibility = View.VISIBLE
                    progressBottom.visibility = View.GONE
                    btnAdd.visibility = View.VISIBLE
                    val negativeFeedbackResponse = response.body() as NegativeFeedbackResponse
                    arrOfNegativeFeedback = negativeFeedbackResponse.arrOfNegativeFeedback
                    setNFAdapter()
                }
            }

            override fun onFailure(call: Call<NegativeFeedbackResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setNFAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvNegative.layoutManager = layoutManager
        negativeFeedbackAdapter = NegativeFeedbackAdapter(context, arrOfNegativeFeedback, this)
        rvNegative.adapter = negativeFeedbackAdapter
    }

    private fun addNegativeFeedback() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addNegativeFeedback(userId, issueComplaints, solution, responsibleId)

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
                    getNegativeFeedback()
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

        progress.visibility = View.VISIBLE
        ivUArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUser()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    rvUser.visibility = View.VISIBLE
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    setUserAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setUserAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUser.layoutManager = layoutManager
        userWithTypeAdapter = UserWithTypeAdapter(context, arrOfUser, this)
        rvUser.adapter = userWithTypeAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.rlBG) {
            if (!arrOfUser[position].isSelected) {
                arrOfUser[position].isSelected = true
                arrOfResponsibleName.add(arrOfUser[position].userName)
                arrOfResponsibleId.add(arrOfUser[position].userId)
                responsibleId = TextUtils.join(",", arrOfResponsibleId)
                tvUser.text = TextUtils.join(" + ", arrOfResponsibleName)
                userWithTypeAdapter.notifyDataSetChanged()

            } else {
                arrOfUser[position].isSelected = false
                arrOfResponsibleName.remove(arrOfUser[position].userName)
                arrOfResponsibleId.remove(arrOfUser[position].userId)
                responsibleId = TextUtils.join(",", arrOfResponsibleId)
                tvUser.text = TextUtils.join(" + ", arrOfResponsibleName)
                userWithTypeAdapter.notifyDataSetChanged()
            }
        }
    }

}
