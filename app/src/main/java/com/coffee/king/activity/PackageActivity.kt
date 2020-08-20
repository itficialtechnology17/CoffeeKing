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
import com.coffee.king.R
import com.coffee.king.adapter.CategoryAdapter
import com.coffee.king.clickcallback.NestedItemClickCallback
import com.coffee.king.model.*
import com.coffee.king.responseCallback.PackageResponse
import com.coffee.king.responseCallback.ResponseStatusCode
import com.coffee.king.rest.ApiClient
import com.coffee.king.rest.ApiInterface
import com.google.gson.Gson
import com.kotharirefrigeration.constant.ConstantKey
import kotlinx.android.synthetic.main.activity_package.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PackageActivity : AppCompatActivity(), NestedItemClickCallback {


    lateinit var context: Context
    var arrOfPackage = ArrayList<ModelPackage>()

    lateinit var categoryAdapter: CategoryAdapter

    private var arrOfSelection = ArrayList<ModelSelection>()
    private var arrOfCategoryPrice = ArrayList<ModelCategoryPrice>()

    var bookingId = ""
    var noOfPeople = 0

    private var totalCategoryPrice = 0
    var modelEventByDate = ModelEventByDate()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package)
        supportActionBar!!.hide()
        context = this
        initDefine()
        initAction()

        if (intent.hasExtra(ConstantKey.KEY_ID)) {
            bookingId = intent.getStringExtra(ConstantKey.KEY_ID)!!
            noOfPeople = intent.getStringExtra(ConstantKey.KEY_STRING)!!.toInt()
        } else {
            btnNext.text = "Update"
            modelEventByDate =
                intent.getSerializableExtra(ConstantKey.KEY_ARRAY) as ModelEventByDate
            bookingId = modelEventByDate.bookingId
            noOfPeople = modelEventByDate.noOfPeople.toInt()
        }
        getPackageList()
    }

    private fun initDefine() {

    }

    private fun initAction() {

        ivBack.setOnClickListener {
            finish()
        }

        btnNext.setOnClickListener {

            btnNext.visibility = View.GONE
            rlLoader.visibility = View.VISIBLE
            val arrOfModelCategoryFood = getCategoryFoodId()
            if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                updatePackage(arrOfModelCategoryFood)
            } else {
                addPackage(arrOfModelCategoryFood)
//                startActivity(
//                    Intent(context, UpgradeEventActivity::class.java)
//                        .putExtra(ConstantKey.KEY_ID, bookingId)
//                        .putExtra(ConstantKey.KEY_STRING, totalCategoryPrice.toString())
//                )
            }
        }

    }

    private fun getCategoryFoodId(): ArrayList<ModelCategoryFood> {
        val arrOfCategoryFood = ArrayList<ModelCategoryFood>()
        for (i in 0 until arrOfSelection.size) {
            val modelCategoryFood = ModelCategoryFood()
            modelCategoryFood.categoryId = arrOfPackage[arrOfSelection[i].parentPosition].categoryId
            modelCategoryFood.categoryPrice =
                arrOfPackage[arrOfSelection[i].parentPosition].categoryPrice
            modelCategoryFood.foodId =
                arrOfPackage[arrOfSelection[i].parentPosition].arrOfFood[arrOfSelection[i].childPosition].foodId
            arrOfCategoryFood.add(modelCategoryFood)

        }
        return arrOfCategoryFood
    }

    private fun addToModelEvent() {
        val arrOfUpdatedPackage = ArrayList<ModelPackage>()
        for (i in 0 until arrOfSelection.size) {
            val modelPackage = ModelPackage()
            modelPackage.categoryPrice =
                arrOfPackage[arrOfSelection[i].parentPosition].categoryPrice
            modelPackage.maximum = arrOfPackage[arrOfSelection[i].parentPosition].maximum
            modelPackage.categoryId = arrOfPackage[arrOfSelection[i].parentPosition].categoryId

            modelPackage.categoryName = arrOfPackage[arrOfSelection[i].parentPosition].categoryName
            val arrOfFood = ArrayList<ModelFood>()
            for (j in 0 until arrOfPackage.size) {
                for (k in 0 until arrOfPackage[j].arrOfFood.size) {
                    if (modelPackage.categoryId == arrOfPackage[j].arrOfFood[k].categoryId && arrOfPackage[j].arrOfFood[k].isSelected) {
                        arrOfFood.add(arrOfPackage[j].arrOfFood[k])
                    }
                }
            }
            modelPackage.arrOfFood = arrOfFood
            if (!checkAlreadyInList(arrOfUpdatedPackage, modelPackage.categoryId)) {
                arrOfUpdatedPackage.add(modelPackage)
            }
        }

        modelEventByDate.arrOfPackage = arrOfUpdatedPackage
        modelEventByDate.totalAmount = totalCategoryPrice.toString()
        val intent = Intent()
        intent.putExtra(ConstantKey.KEY_ARRAY, modelEventByDate)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun checkAlreadyInList(
        arrOfUpdatedPackage: ArrayList<ModelPackage>,
        categoryId: String
    ): Boolean {
        var isExist = false
        for (i in 0 until arrOfUpdatedPackage.size) {
            isExist = arrOfUpdatedPackage[i].categoryId == categoryId
        }
        return isExist
    }

    private fun getPackageList() {

        slRecycle.startShimmerAnimation()
        rvPackageList.visibility = View.VISIBLE

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.getPackage()

        call.enqueue(object : Callback<PackageResponse> {
            override fun onResponse(
                call: Call<PackageResponse>,
                response: Response<PackageResponse>
            ) {

                if (response.body() != null) {
                    slRecycle.visibility = View.GONE
                    rvPackageList.visibility = View.VISIBLE
                    val packageResponse = response.body() as PackageResponse
                    arrOfPackage = packageResponse.arrOfPackage
                    if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                        setUpdateDetails()
                    } else {
                        setPackageAdapter()
                    }

                }
            }

            override fun onFailure(call: Call<PackageResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }

    private fun setUpdateDetails() {
        for (i in 0 until arrOfPackage.size) {
            for (j in 0 until arrOfPackage[i].arrOfFood.size) {
                for (k in 0 until modelEventByDate.arrOfPackage.size) {
                    for (p in 0 until modelEventByDate.arrOfPackage[k].arrOfFood.size) {
                        if (modelEventByDate.arrOfPackage[k].arrOfFood[p].foodId == arrOfPackage[i].arrOfFood[j].foodId) {
                            arrOfPackage[i].arrOfFood[j].isSelected = true
                            val modelSelection = ModelSelection()
                            modelSelection.parentPosition = i
                            modelSelection.childPosition = j
                            arrOfSelection.add(modelSelection)
                            totalCategoryPrice = modelEventByDate.totalAmount.toInt()
                            tvPackagePrice.text = "Rs. $totalCategoryPrice"
                        }
                    }
                }
            }
        }

        setPackageAdapter()
        setCategoryPrice()
    }

    private fun setCategoryPrice() {
        for (i in 0 until modelEventByDate.arrOfPackage.size) {
            for (j in 0 until modelEventByDate.arrOfPackage[i].arrOfFood.size) {
                val modelCategoryPrice = ModelCategoryPrice()
                modelCategoryPrice.categoryPrice = modelEventByDate.arrOfPackage[i].categoryPrice
                modelCategoryPrice.categoryId = modelEventByDate.arrOfPackage[i].categoryId
                modelCategoryPrice.foodId = modelEventByDate.arrOfPackage[i].arrOfFood[j].foodId
                arrOfCategoryPrice.add(modelCategoryPrice)
            }
        }
    }

    private fun setPackageAdapter() {
        val layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        rvPackageList.layoutManager = layoutManager
        categoryAdapter = CategoryAdapter(context, arrOfPackage, this, 1)
        rvPackageList.adapter = categoryAdapter
    }

    override fun onNestedItemClick(view: View, parentPosition: Int, childPosition: Int) {
        if (view.id == R.id.ivArrow || view.id == R.id.rlCategory) {
            arrOfPackage[parentPosition].isSelected = !arrOfPackage[parentPosition].isSelected
        } else if (view.id == R.id.rlContentBG) {
            if (!arrOfPackage[parentPosition].arrOfFood[childPosition].isSelected) {
                if (getSelectionSize(parentPosition) < arrOfPackage[parentPosition].maximum.toInt()) {
                    arrOfPackage[parentPosition].arrOfFood[childPosition].isSelected = true
                    val modelSelection = ModelSelection()
                    modelSelection.parentPosition = parentPosition
                    modelSelection.childPosition = childPosition
                    arrOfSelection.add(modelSelection)
                    addToCategoryPrice(arrOfPackage[parentPosition], childPosition)
                    updatePrice()
                } else {
                    Toast.makeText(
                        context,
                        "Maximum selection " + arrOfPackage[parentPosition].maximum,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                arrOfPackage[parentPosition].arrOfFood[childPosition].isSelected = false
                removeSelectedItem(parentPosition, childPosition)
                removeFromCategoryPrice(arrOfPackage[parentPosition], childPosition)
                updatePrice()
            }

        }
        categoryAdapter.notifyDataSetChanged()
    }

    private fun updatePrice() {
        var price = 0
        for (i in 0 until arrOfPackage.size) {
            for (j in 0 until arrOfPackage[i].arrOfFood.size) {
                if (arrOfPackage[i].arrOfFood[j].isSelected) {
                    price += arrOfPackage[i].categoryPrice.toInt() * noOfPeople
                    break
                }
            }
        }

        totalCategoryPrice = price

        if (modelEventByDate.bookingType == "1") {
            var upgradeEventPrice = 0
            for (i in 0 until modelEventByDate.arrOfUpgradeEvent.size) {
                upgradeEventPrice += modelEventByDate.arrOfUpgradeEvent[i].upgradeEventPrice.toInt()
            }
            totalCategoryPrice += upgradeEventPrice
        } else {
            var upgradeCouplePackagePrice = 0
            for (i in 0 until modelEventByDate.arrOfCouplePackage.size) {
                upgradeCouplePackagePrice += modelEventByDate.arrOfCouplePackage[i].packagePrice.toInt()
            }
            totalCategoryPrice += upgradeCouplePackagePrice
        }

//        val categoryPrice = modelPackage.categoryPrice.toInt() * noOfPeople;
//        totalCategoryPrice = price
        tvPackagePrice.text = "Rs. $totalCategoryPrice"
    }

    private fun removeFromCategoryPrice(modelPackage: ModelPackage, childPosition: Int) {
        for (i in 0 until arrOfCategoryPrice.size) {
            if (arrOfCategoryPrice[i].categoryId == modelPackage.categoryId
                && arrOfCategoryPrice[i].foodId == modelPackage.arrOfFood[childPosition].foodId
            ) {
                arrOfCategoryPrice.removeAt(i)
                break
            }
        }

//
//        var isCategoryInclude = false;
//        for (i in 0 until arrOfCategoryPrice.size) {
//            if (arrOfCategoryPrice[i].categoryId == modelPackage.categoryId) {
//                isCategoryInclude = true
//                break
//            }
//        }
//
//        if (!isCategoryInclude) {
//            val categoryPrice = modelPackage.categoryPrice.toInt() * noOfPeople;
//            totalCategoryPrice -= categoryPrice
//            tvPackagePrice.text = "Rs. $totalCategoryPrice"
//        }

    }


    private fun addToCategoryPrice(
        modelPackage: ModelPackage,
        childPosition: Int
    ) {
        if (arrOfCategoryPrice.isNotEmpty()) {
            var isContain = false
            for (i in 0 until arrOfCategoryPrice.size) {
                isContain = arrOfCategoryPrice[i].categoryId == modelPackage.categoryId
            }
            if (!isContain) {
                val modelCategoryPrice = ModelCategoryPrice()
                modelCategoryPrice.categoryId = modelPackage.categoryId
                modelCategoryPrice.categoryPrice = modelPackage.categoryPrice
                modelCategoryPrice.foodId = modelPackage.arrOfFood[childPosition].foodId
                arrOfCategoryPrice.add(modelCategoryPrice)

                val categoryPrice = modelCategoryPrice.categoryPrice.toInt() * noOfPeople

                totalCategoryPrice += categoryPrice
                tvPackagePrice.text = "Rs. $totalCategoryPrice"
            }
        } else {
            val modelCategoryPrice = ModelCategoryPrice()
            modelCategoryPrice.categoryId = modelPackage.categoryId
            modelCategoryPrice.categoryPrice = modelPackage.categoryPrice
            modelCategoryPrice.foodId = modelPackage.arrOfFood[childPosition].foodId
            arrOfCategoryPrice.add(modelCategoryPrice)

            val categoryPrice = modelCategoryPrice.categoryPrice.toInt() * noOfPeople
            totalCategoryPrice = categoryPrice
            if (intent.hasExtra(ConstantKey.KEY_ARRAY)) {
                var upgradePrice = 0
                if (modelEventByDate.arrOfUpgradeEvent.isNotEmpty()) {
                    for (i in 0 until modelEventByDate.arrOfUpgradeEvent.size) {
                        upgradePrice += modelEventByDate.arrOfUpgradeEvent[i].upgradeEventPrice.toInt()
                    }
                }
                if (modelEventByDate.arrOfCouplePackage.isNotEmpty()) {
                    for (i in 0 until modelEventByDate.arrOfCouplePackage.size) {
                        upgradePrice += modelEventByDate.arrOfCouplePackage[i].packagePrice.toInt()
                    }
                }
                totalCategoryPrice += upgradePrice
            }
            tvPackagePrice.text = "Rs. $totalCategoryPrice"
        }
    }

    private fun removeSelectedItem(parentPosition: Int, childPosition: Int) {
        for (i in 0 until arrOfSelection.size) {
            if (arrOfSelection[i].parentPosition == parentPosition && arrOfSelection[i].childPosition == childPosition) {
                arrOfSelection.removeAt(i)
                break
            }
        }
    }

    private fun getSelectionSize(parentPosition: Int): Int {
        var count = 0
        for (i in 0 until arrOfSelection.size) {
            if (arrOfSelection[i].parentPosition == parentPosition) {
                count += 1
            }
        }
        return count
    }

    private fun addPackage(arrOfModelCategoryFood: ArrayList<ModelCategoryFood>) {

        val selectionJson = Gson().toJson(arrOfModelCategoryFood)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.addPackage(bookingId, selectionJson, totalCategoryPrice)

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    rlLoader.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    val responseStatusCode = response.body() as ResponseStatusCode
//                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    if (responseStatusCode.statusCode == 101) {
                        startActivity(
                            Intent(context, UpgradeEventActivity::class.java).putExtra(
                                ConstantKey.KEY_ID,
                                bookingId
                            ).putExtra(ConstantKey.KEY_STRING, totalCategoryPrice.toString())
                        )
                        finish()
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

    private fun updatePackage(arrOfModelCategoryFood: ArrayList<ModelCategoryFood>) {

        val selectionJson = Gson().toJson(arrOfModelCategoryFood)

        val apiInterface = ApiClient.client.create(ApiInterface::class.java)
        val call = apiInterface.updatePackage(
            bookingId,
            selectionJson,
            totalCategoryPrice,
            modelEventByDate.advancePaidAmount
        )

        call.enqueue(object : Callback<ResponseStatusCode> {
            override fun onResponse(
                call: Call<ResponseStatusCode>,
                response: Response<ResponseStatusCode>
            ) {
                if (response.body() != null) {
                    rlLoader.visibility = View.GONE
                    btnNext.visibility = View.VISIBLE
                    val responseStatusCode = response.body() as ResponseStatusCode
                    Toast.makeText(context, responseStatusCode.message, Toast.LENGTH_SHORT).show()
                    addToModelEvent()

                }
            }

            override fun onFailure(call: Call<ResponseStatusCode>, t: Throwable) {
                rlLoader.visibility = View.GONE
                btnNext.visibility = View.VISIBLE
                t.printStackTrace()
            }
        })
    }

    class ModelCategoryPrice {
        var categoryId = ""
        var categoryPrice = ""
        var foodId = ""
    }
}
