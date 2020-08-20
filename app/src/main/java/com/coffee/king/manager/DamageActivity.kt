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
import com.coffee.king.adapter.DamageDestroyAdapter
import com.coffee.king.adapter.InChargeAdapter
import com.coffee.king.adminadapter.UserWithTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelDamageDestroy
import com.coffee.king.model.ModelUser
import com.coffee.king.responseCallback.DamageDestroyResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_damage.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DamageActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfUser = ArrayList<ModelUser>()
    var arrOfInCharge = ArrayList<ModelUser>()
    private var arrOfResponsibleName = ArrayList<String>()
    private var arrOfResponsibleId = ArrayList<String>()
    var arrOfDamageDestroy=ArrayList<ModelDamageDestroy>()
    private lateinit var userWithTypeAdapter: UserWithTypeAdapter
    private lateinit var inChargeAdapter: InChargeAdapter
    lateinit var damageDestroyAdapter: DamageDestroyAdapter


    var damageDestroyId = ""
    var itemName = ""
    var reason = ""
    var responsibleName = ""
    var responsibleId = ""
    var inChargeId = ""
    var contactNumber = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_damage)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
    }

    private fun initDefine() {
        getUser()
        getDamageDestroy()
    }


    private fun initAction() {

        ivBack.setOnClickListener {
            onBackPressed()
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

        rlInChargeUser.setOnClickListener {
            if (arrOfInCharge.isEmpty()) {
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

        rgType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCustomer -> {
                    etName.visibility = View.VISIBLE
                    llUser.visibility = View.GONE
                }
                R.id.rbStaff -> {
                    etName.visibility = View.GONE
                    llUser.visibility = View.VISIBLE
                }
            }
        }

        btnAdd.setOnClickListener {
            rlList.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
        }

        btnSubmit.setOnClickListener {
            itemName = etItemName.text.toString()
            reason = etReason.text.toString()
            responsibleName = etName.text.toString()
            contactNumber = etContact.text.toString()

            if (damageDestroyId.isEmpty()) {
                when {
                    itemName.isEmpty() -> Toast.makeText(
                        context,
                        "Please Enter Item Name",
                        Toast.LENGTH_SHORT
                    ).show()

                    reason.isEmpty() -> Toast.makeText(
                        context,
                        "Please Enter Reason",
                        Toast.LENGTH_SHORT
                    ).show()

                    contactNumber.isEmpty() -> Toast.makeText(
                        context,
                        "Please Enter Contact Number",
                        Toast.LENGTH_SHORT
                    ).show()

                    inChargeId.isEmpty() ->
                        Toast.makeText(
                            context,
                            "Please Select In Charge ",
                            Toast.LENGTH_SHORT
                        ).show()

                    else -> addDamageDestroy()
                }
            }
        }
    }

    private fun addDamageDestroy() {

        rlLoader.visibility = View.VISIBLE
        btnSubmit.visibility = View.GONE

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call =
            apiInterface.addDamageDestroy(
                userId,
                itemName,
                reason,
                responsibleName,
                responsibleId,
                inChargeId,
                contactNumber
            )

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
                    getDamageDestroy()
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnSubmit.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun getDamageDestroy() {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getDamageDestroy()

        call.enqueue(object : Callback<DamageDestroyResponse> {
            override fun onResponse(
                call: Call<DamageDestroyResponse>,
                response: Response<DamageDestroyResponse>
            ) {
                if (response.body() != null) {
                    val damageDestroyResponse = response.body() as DamageDestroyResponse
                    arrOfDamageDestroy = damageDestroyResponse.arrOfDamageDestroy
                    setLostFoundAdapter()
                }
            }

            override fun onFailure(call: Call<DamageDestroyResponse>, t: Throwable) {
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setLostFoundAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvDamageDestroy.layoutManager = layoutManager
        damageDestroyAdapter = DamageDestroyAdapter(context, arrOfDamageDestroy, this)
        rvDamageDestroy.adapter = damageDestroyAdapter
    }

    override fun onBackPressed() {
        if (rlContent.visibility == View.VISIBLE) {
            rlList.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
        } else {
            super.onBackPressed()
        }

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
                progressInCharge.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                ivIArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    rvUser.visibility = View.VISIBLE
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    arrOfInCharge = userResponse.arrOfUser
                    setUserAdapter()
                    setInChargeAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                progressInCharge.visibility = View.GONE
                progress.visibility = View.GONE
                ivUArrow.visibility = View.VISIBLE
                ivIArrow.visibility = View.VISIBLE
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

    private fun setInChargeAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvInChargeUser.layoutManager = layoutManager
        inChargeAdapter = InChargeAdapter(context, arrOfInCharge, this)
        rvInChargeUser.adapter = inChargeAdapter
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
        } else if (view.id == R.id.rlBGInCharge) {
            if (!arrOfInCharge[position].isSelected) {
                for (i in 0 until arrOfInCharge.size) {
                    arrOfInCharge[i].isSelected = false
                }
                tvInChargeUser.text = arrOfInCharge[position].userName
                arrOfInCharge[position].isSelected = true
                inChargeId = arrOfInCharge[position].userId
            } else {
                arrOfInCharge[position].isSelected = false
                inChargeId = ""
                tvInChargeUser.text = ""
            }
            inChargeAdapter.notifyDataSetChanged()
        }
    }
}
