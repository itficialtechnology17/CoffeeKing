package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelPackage : Serializable {

    @SerializedName("categoryId")
    var categoryId=""

    @SerializedName("categoryName")
    var categoryName=""

    @SerializedName("categoryPrice")
    var categoryPrice=""

    @SerializedName("maximum")
    var maximum=""

    @SerializedName("food")
    var arrOfFood=ArrayList<ModelFood>()

    var isSelected=false

}