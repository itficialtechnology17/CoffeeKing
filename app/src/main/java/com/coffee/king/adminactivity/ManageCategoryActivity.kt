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
import com.coffee.king.adminadapter.AdminCategoryAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCategory
import com.coffee.king.responseCallback.CategoryResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_manage_category.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageCategoryActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfCategory = ArrayList<ModelCategory>()
    lateinit var adminCategoryAdapter: AdminCategoryAdapter

    var position=0
    var categoryId = ""
    var categoryName = ""
    var categoryPrice = ""
    var maximum = ""

    var isInsert = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_category)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if (Utils.isOnline(context)) {
            getCategory()
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
            etCategory.setText("")
            etPrice.setText("")
            etMaximum.setText("")
            categoryId=""
        }

        btnAddUpdate.setOnClickListener {

            categoryName = etCategory.text.toString()
            categoryPrice = etPrice.text.toString()
            maximum = etMaximum.text.toString()

            if (categoryName.isEmpty()) {
                Toast.makeText(context, "Enter Category Name", Toast.LENGTH_SHORT).show()
            } else if (categoryPrice.isEmpty()) {
                Toast.makeText(context, "Enter Price", Toast.LENGTH_SHORT).show()
            } else if (maximum.isEmpty()) {
                Toast.makeText(context, "Enter Maximum Selection", Toast.LENGTH_SHORT).show()
            } else {
                addUpdateCategory()
            }
        }
    }

    private fun getCategory() {

        slRecycle.visibility = View.VISIBLE
        slRecycle.startShimmerAnimation()
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCategoryList()

        call.enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>
            ) {

                if (response.body() != null) {
                    slRecycle.visibility = View.GONE
                    rvCategory.visibility = View.VISIBLE
                    val categoryResponse = response.body() as CategoryResponse
                    arrOfCategory = categoryResponse.arrOfCategory
                    setAdapter()
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCategory.layoutManager = layoutManager
        adminCategoryAdapter =
            AdminCategoryAdapter(context, arrOfCategory, this)
        rvCategory.adapter = adminCategoryAdapter
    }

    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.ivEdit) {
            setEditData(position)
        } else if (view.id == R.id.ivDelete) {
            onClickDelete(position)
        }
    }

    private fun onClickDelete(position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Sure to delete category "+arrOfCategory[position].categoryName+" ?")
            .setNegativeButton(android.R.string.no) { dialog, i ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteCategory(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position=position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        categoryId = arrOfCategory[position].categoryId
        etCategory.setText(arrOfCategory[position].categoryName)
        etPrice.setText(arrOfCategory[position].categoryPrice)
        etMaximum.setText(arrOfCategory[position].maximum)
        btnAddUpdate.text = "Update"
    }

    private fun addUpdateCategory() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateCategory(
            userId, categoryId, categoryName, categoryPrice, maximum)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                rlLoader.visibility = View.GONE
                btnAddUpdate.visibility = View.VISIBLE
                rlContent.visibility=View.VISIBLE
                rlAddUpdate.visibility=View.GONE
                ivAdd.visibility=View.VISIBLE
                if (response.body() != null) {
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if(categoryId==""){
                        val modelCategory=ModelCategory()
                        modelCategory.categoryId=responseStatusCode.id
                        modelCategory.categoryName=categoryName
                        modelCategory.categoryPrice=categoryPrice
                        modelCategory.maximum=maximum
                        arrOfCategory.add(modelCategory)
                        adminCategoryAdapter.notifyDataSetChanged()
                    }else{
                        arrOfCategory[position].categoryName=categoryName
                        arrOfCategory[position].categoryPrice=categoryPrice
                        arrOfCategory[position].maximum=maximum
                        adminCategoryAdapter.notifyDataSetChanged()
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


    private fun deleteCategory(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteCategory(arrOfCategory[position].categoryId)
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
                        arrOfCategory.removeAt(position)
                        adminCategoryAdapter.notifyDataSetChanged()
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
