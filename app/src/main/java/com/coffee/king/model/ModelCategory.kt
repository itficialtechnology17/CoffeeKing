package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelCategory : Serializable {

    @SerializedName("categoryId")
    var categoryId=""

    @SerializedName("categoryName")
    var categoryName=""

    @SerializedName("categoryPrice")
    var categoryPrice=""

    @SerializedName("maximum")
    var maximum=""

    var isSelected=false

}