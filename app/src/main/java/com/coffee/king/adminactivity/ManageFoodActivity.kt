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
import com.coffee.king.adminadapter.FoodCategoryAdapter
import com.coffee.king.adminadapter.AdminFoodAdapter
import com.coffee.king.clickcallback.ItemClickCallback
import com.coffee.king.model.ModelCategory
import com.coffee.king.model.ModelFood
import com.coffee.king.responseCallback.CategoryResponse
import com.coffee.king.responseCallback.FoodResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.coffee.king.utils.Utils
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_manage_category.btnAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.ivAdd
import kotlinx.android.synthetic.main.activity_manage_category.ivBack
import kotlinx.android.synthetic.main.activity_manage_category.rlAddUpdate
import kotlinx.android.synthetic.main.activity_manage_category.rlContent
import kotlinx.android.synthetic.main.activity_manage_category.rlLoader
import kotlinx.android.synthetic.main.activity_manage_category.rvCategory
import kotlinx.android.synthetic.main.activity_manage_category.slRecycle
import kotlinx.android.synthetic.main.activity_manage_food.*
import kotlinx.android.synthetic.main.cell_of_category.rvFood
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ManageFoodActivity : AppCompatActivity(), ItemClickCallback {


    lateinit var context: Context

    var arrOfFood = ArrayList<ModelFood>()
    var arrOfCategory = ArrayList<ModelCategory>()
    lateinit var adminFoodAdapter: AdminFoodAdapter
    lateinit var foodCategoryAdapter: FoodCategoryAdapter

    var position=0
    var foodId = ""
    var foodName = ""
    var categoryId = ""


    var isInsert=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_food)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()
        if(Utils.isOnline(context)) {
            getFoodList()
            getCategory()
        }else{
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
            foodId=""

            isInsert=true
            etFoodName.setText("")
            tvCategoryName.text="Select Category"

            for(i in 0 until arrOfCategory.size){
                arrOfCategory[i].isSelected=false
            }
            foodCategoryAdapter.notifyDataSetChanged()
        }

        btnAddUpdate.setOnClickListener {

            foodName = etFoodName.text.toString()
            when {
                foodName.isEmpty() -> Toast.makeText(context, "Enter Food Name", Toast.LENGTH_SHORT).show()
                isInsert && categoryId.isEmpty() ->  Toast.makeText(context, "Select Category", Toast.LENGTH_SHORT).show()
                else -> addUpdateFood()
            }
        }
    }

    private fun getFoodList() {

        slRecycle.visibility = View.VISIBLE
        slRecycle.startShimmerAnimation()
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getFoodList()

        call.enqueue(object : Callback<FoodResponse> {
            override fun onResponse(
                call: Call<FoodResponse>,
                response: Response<FoodResponse>
            ) {
                rvFood.visibility=View.VISIBLE
                slRecycle.visibility = View.GONE
                if (response.body() != null) {
                    rvFood.visibility=View.VISIBLE
                    slRecycle.visibility = View.GONE
                    val foodResponse = response.body() as FoodResponse
                    arrOfFood = foodResponse.arrOfFood
                    setAdapter()
                }
            }

            override fun onFailure(call: Call<FoodResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun getCategory() {

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getCategoryList()

        call.enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(
                call: Call<CategoryResponse>,
                response: Response<CategoryResponse>
            ) {

                if (response.body() != null) {
                    val categoryResponse = response.body() as CategoryResponse
                    arrOfCategory = categoryResponse.arrOfCategory
                    setCategoryAdapter()
                }
            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvFood.layoutManager = layoutManager
        adminFoodAdapter =
            AdminFoodAdapter(context, arrOfFood, this)
        rvFood.adapter = adminFoodAdapter
    }

    private fun setCategoryAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvCategory.layoutManager = layoutManager
        foodCategoryAdapter =
            FoodCategoryAdapter(context, arrOfCategory, this)
        rvCategory.adapter = foodCategoryAdapter
    }


    override fun onItemClick(view: View, position: Int) {
        if (view.id == R.id.ivEdit) {
            setEditData(position)
        } else if (view.id == R.id.ivDelete) {
            onClickDelete(position)
        } else if (view.id == R.id.rlContentBG) {
            if(!arrOfCategory[position].isSelected){
                for(i in 0 until arrOfCategory.size){
                    arrOfCategory[i].isSelected=false
                }
                arrOfCategory[position].isSelected =true
                categoryId=arrOfCategory[position].categoryId
                tvCategoryName.text=arrOfCategory[position].categoryName
                foodCategoryAdapter.notifyDataSetChanged()
            }
        }
    }

    private fun onClickDelete(position: Int) {
        AlertDialog.Builder(this)
            .setMessage("Sure to delete food "+arrOfFood[position].foodName+" ?")
            .setNegativeButton(android.R.string.no) { dialog, i ->
                dialog.dismiss()
            }
            .setPositiveButton(android.R.string.yes) { dialog, i ->
                dialog.dismiss()
                deleteFood(position)
            }
            .create()
            .show()
    }

    private fun setEditData(position: Int) {
        this.position=position
        rlAddUpdate.visibility = View.VISIBLE
        rlContent.visibility = View.GONE
        ivAdd.visibility = View.GONE

        isInsert=false
        foodId = arrOfFood[position].foodId
        categoryId=arrOfFood[position].categoryId
        etFoodName.setText(arrOfFood[position].foodName)

        for(i in 0 until arrOfCategory.size){
            if(arrOfFood[position].categoryId==arrOfCategory[i].categoryId){
                tvCategoryName.text=arrOfCategory[i].categoryName
                break
            }
        }

        btnAddUpdate.text = "Update"
    }

    private fun addUpdateFood() {

        val userId = Utils.getPref(context, ConstantKey.KEY_LOGIN_ID, "")!!

        rlLoader.visibility = View.VISIBLE
        btnAddUpdate.visibility = View.GONE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addUpdateFood(
            userId, foodId, foodName, categoryId)

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
                    if(foodId==""){
                        val modelCategory=ModelFood()
                        modelCategory.foodId=responseStatusCode.id
                        modelCategory.foodName=foodName
                        modelCategory.categoryId=categoryId
                        arrOfFood.add(modelCategory)
                        adminFoodAdapter.notifyDataSetChanged()
                    }else{
                        arrOfFood[position].foodName=foodName
                        arrOfFood[position].categoryId=categoryId
                        adminFoodAdapter.notifyDataSetChanged()
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

    private fun deleteFood(position: Int) {
        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.deleteFood(arrOfFood[position].foodId)
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
                        arrOfFood.removeAt(position)
                        adminFoodAdapter.notifyDataSetChanged()
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
