package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCategory
import com.coffee.king.model.ModelEventType
import com.coffee.king.model.ModelFood
import com.coffee.king.model.ModelPackage
import com.google.gson.annotations.SerializedName

class FoodResponse {

    @SerializedName("food")
    var arrOfFood=ArrayList<ModelFood>()
}