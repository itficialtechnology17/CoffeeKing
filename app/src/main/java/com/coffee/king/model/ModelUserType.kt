package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelUserType : Serializable {

    @SerializedName("user_type_id")
    var userTypeId=""

    @SerializedName("user_type")
    var userType=""

    var isSelected=false
}