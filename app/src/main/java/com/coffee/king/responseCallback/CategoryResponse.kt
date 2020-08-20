package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCategory
import com.coffee.king.model.ModelEventType
import com.coffee.king.model.ModelPackage
import com.google.gson.annotations.SerializedName

class CategoryResponse {

    @SerializedName("category")
    var arrOfCategory=ArrayList<ModelCategory>()
}