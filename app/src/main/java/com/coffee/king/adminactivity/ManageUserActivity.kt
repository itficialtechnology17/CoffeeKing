package com.coffee.king.adminactivity

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.coffee.king.R
import com.coffee.king.adminadapter.CafeLocationAdapter
import com.coffee.king.adminadapter.UserAdapter
import com.coffee.king.adminadapter.UserTypeAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCafeLocation
import com.coffee.king.model.ModelUser
import com.coffee.king.model.ModelUserType
import com.coffee.king.responseCallback.CafeLocationResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.responseCallback.UserResponse
import com.coffee.king.responseCallback.UserTypeResponse
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import kotlinx.android.synthetic.main.activity_manage_category.btnAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.ivAdd
import kotlinx.android.synthetic.main.activity_manage_category.ivBack
import kotlinx.android.synthetic.main.activity_manage_category.rlAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.rlContent
import kotlinx.android.synthetic.main.activity_manage_category.rlLoader
import kotlinx.android.synthetic.main.activity_manage_category.slRecycle
import kotlinx.android.synthetic.main.activity_manage_user.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageUserActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfUser = ArrayList<ModelUser>()
    var arrOfUserType = ArrayList<ModelUserType>()
    var arrOfCafeLocation = ArrayList<ModelCafeLocation>()

    lateinit var userAdapter: UserAdapter
    lateinit var userTypeAdapter: UserTypeAdapter
    lateinit var cafeLocationAdapter:CafeLocationAdapter

    var position = 0
    var userId = ""
    var userName = ""
    var userMobile = ""
    var userPassword = ""
    var userTypeId = ""
    var userTypeName = ""
    var locationId = ""
    var locationName = ""
    var isActive = 1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_user)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if (Utils.isOnline(context)) {
            getUser()
            getUserType()
            getCafeLocation()
        } else {
            Toast.makeText(context, "No Internet Connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initDefine() {

    }

    private fun initAction() {
        ivBack.setOnClickListener {
            if (rlAddUpdate.visibility == View.VISIBLE) {
                rlAddUpdate.visibility = View.GONE
                rlContent.visibility = View.VISIBLE
                ivAdd.visibility = View.VISIBLE
            } else {
                finish()
            }
        }

        ivAdd.setOnClickListener {
            rlAddUpdate.visibility = View.VISIBLE
            rlContent.visibility = View.GONE
            ivAdd.visibility = View.GONE
            btnAddUpdate.text = "Add"
            userId = ""

            etUserName.setText("")
            etMobileNumber.setText("")
            etPassword.setText("")
            isActive = 1
            switchUser.isChecked = true

        }

        btnAddUpdate.setOnClickListener {

            userName = etUserName.text.toString()
            userMobile = etMobileNumber.text.toString()
            userPassword = etPassword.text.toString()

            if (userName.isEmpty()) {
                Toast.makeText(context, "Enter User Name", Toast.LENGTH_SHORT).show()
            } else if (userMobile.isEmpty()) {
                Toast.makeText(context, "Enter Mobile Number", Toast.LENGTH_SHORT).show()
            } else if (userPassword.isEmpty()) {
                Toast.makeText(context, "Enter Password", Toast.LENGTH_SHORT).show()
            } else if (userTypeId == "") {
                Toast.makeText(context, "Select User Type", Toast.LENGTH_SHORT).show()
            } else {
                addUpdateUser()
            }
        }

        switchUser.setOnCheckedChangeListener { buttonView, isChecked ->
            isActive = if (isChecked) {
                1
            } else {
                0
            }
        }

        rlEventType.setOnClickListener {
            if (arrOfUserType.isEmpty()) {
                getUserType()
            }
            if (progress.visibility != View.VISIBLE) {
                if (viewUserType.visibility == View.GONE) {
                    viewUserType.visibility = View.VISIBLE
                    ivUTArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewUserType.visibility = View.GONE
                    ivUTArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }

        rlCafeLocation.setOnClickListener {
            if (arrOfCafeLocation.isEmpty()) {
                getCafeLocation()
            }
            if (progressCafeLocation.visibility != View.VISIBLE) {
                if (viewCafeLocation.visibility == View.GONE) {
                    viewCafeLocation.visibility = View.VISIBLE
                    ivCLArrow.setImageResource(R.drawable.ic__arrow_up)
                } else {
                    viewCafeLocation.visibility = View.GONE
                    ivCLArrow.setImageResource(R.drawable.ic_arrow_down)
                }
            }
        }
    }

    private fun getUser() {

        slRecycle.startShimmerAnimation()
        slRecycle.visibility = View.VISIBLE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUser()

        call.enqueue(object : Callback<UserResponse> {
            override fun onResponse(
                call: Call<UserResponse>,
                response: Response<UserResponse>
            ) {
                if (response.body() != null) {
                    slRecycle.visibility = View.GONE
                    rvUser.visibility = View.VISIBLE
                    val userResponse = response.body() as UserResponse
                    arrOfUser = userResponse.arrOfUser
                    setUserAdapter()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun getUserType() {

        progress.visibility = View.VISIBLE
        ivUTArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getUserType()

        call.enqueue(object : Callback<UserTypeResponse> {
            override fun onResponse(
                call: Call<UserTypeResponse>,
                response: Response<UserTypeResponse>
            ) {
                progress.visibility = View.GONE
                ivUTArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val userTypeResponse = response.body() as UserTypeResponse
                    arrOfUserType = userTypeResponse.arrOfUserType
                    setUserTypeAdapter()
                }
            }

            override fun onFailure(call: Call<UserTypeResponse>, t: Throwable) {
                progress.visibility = View.GONE
                ivUTArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setUserTypeAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUserType.layoutManager = layoutManager
        userTypeAdapter = UserTypeAdapter(context, arrOfUserType, this)
        rvUserType.adapter = userTypeAdapter
    }

    private fun setUserAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvUser.layoutManager = layoutManager
        userAdapter = UserAdapter(context, arrOfUser, this)
        rvUser.adapter = userAdapter
    }

    private fun getCafeLocation() {

        progressCafeLocation.visibility = View.VISIBLE
        ivCLArrow.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCafeLocation()

        call.enqueue(object : Callback<CafeLocationResponse> {
            override fun onResponse(
                call: Call<CafeLocationResponse>,
                response: Response<CafeLocationResponse>
            ) {
                progressCafeLocation.visibility = View.GONE
                ivCLArrow.visibility = View.VISIBLE
                if (response.body() != null) {
                    val cafeLocationResponse = response.body() as CafeLocationResponse
                    arrOfCafeLocation = cafeLocationResponse.arrOfCafeLocation
                    setCafeLocationAdapter()
                }
            }

            override fun onFailure(call: Call<CafeLocationResponse>, t: Throwable) {
                progressCafeLocation.visibility = View.GONE
                ivCLArrow.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    private fun setCafeLocationAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCafeLocation.layoutManager = layoutManager
        cafeLocationAdapter = CafeLocationAdapter(context, arrOfCafeLocation, this)
        rvCafeLocation.adapter = cafeLocationAdapter
    }


    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.ivEdit) {
            setEditData(position)
        } else if (view.id == R.id.ivDelete) {
            onClickDelete(position)
        }else if(view.id==R.id.rlContentBG){
            viewUserType.visibility=View.GONE
            userTypeId=arrOfUserType[position].userTypeId
            userTypeName=arrOfUserType[position].userType
            tvUserType.text=arrOfUserType[position].userType
        }else if(view.id==R.id.rlCafeContentBG){
            viewCafeLocation.visibility=View.GONE
            locationId=arrOfCafeLocation[position].cafeLocationId
            locationName=arrOfCafeLocation[position].cafeLocation
            tvCafeLocation.text=locationName
        }
    }

    private fun onClickDelete(position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Sure to delete user " + arrOfUser[position].userName + " ?")
            .setNegativeButton(android.R.string.no) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteUser(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position = position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        userId = arrOfUser[position].userId
        userTypeId=arrOfUser[position].userTypeId
        tvUserType.text=arrOfUser[position].userTypeName

        locationId=arrOfUser[position].locationId
        locationName=arrOfUser[position].locationName
        tvCafeLocation.text=locationName

        etUserName.setText(arrOfUser[position].userName)
        etMobileNumber.setText(arrOfUser[position].userMobile)
        etPassword.setText(arrOfUser[position].userPassword)
        switchUser.isChecked = arrOfUser[position].isActive != 0
        btnAddUpdate.text = "Update"

    }

    private fun addUpdateUser() {

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateUser(userId, userName, userMobile, userPassword,userTypeId,locationId, isActive)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                rlContent.visibility = View.VISIBLE
                rlAddUpdate.visibility = View.GONE
                ivAdd.visibility = View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (userId == "") {
                        val modelUser = ModelUser()
                        modelUser.userId = responseStatusCode.id
                        modelUser.userName = userName
                        modelUser.userMobile = userMobile
                        modelUser.userPassword = userPassword
                        modelUser.userTypeId = userTypeId
                        modelUser.isActive = isActive
                        arrOfUser.add(modelUser)
                        userAdapter.notifyDataSetChanged()
                    } else {
                        arrOfUser[position].userName = userName
                        arrOfUser[position].userMobile = userMobile
                        arrOfUser[position].userPassword = userPassword
                        arrOfUser[position].userTypeId=userTypeId
                        arrOfUser[position].userTypeName=userTypeName
                        arrOfUser[position].isActive = isActive
                        userAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }


    private fun deleteUser(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteUser(arrOfUser[position].userId)
        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    if (responseStatusCode.statusCode == 101) {
                        Toast.makeText(
                            context,
                            responseStatusCode.message,
                            Toast.LENGTH_SHORT
                        ).show()
                        arrOfUser.removeAt(position)
                        userAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            context,
                            responseStatusCode.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    override fun onBackPressed() {
        if (rlAddUpdate.visibility == View.VISIBLE) {
            rlAddUpdate.visibility = View.GONE
            rlContent.visibility = View.VISIBLE
            ivAdd.visibility = View.VISIBLE
        } else {
            super.onBackPressed()
        }
    }


}
