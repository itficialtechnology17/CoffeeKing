package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelFood : Serializable {

    @SerializedName("foodId")
    var foodId=""

    @SerializedName("foodName")
    var foodName=""

    @SerializedName("categoryId")
    var categoryId=""

    var isSelected=false
}