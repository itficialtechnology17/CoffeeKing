package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelDate : Serializable {

    @SerializedName("date")
    var date=""

    @SerializedName("day")
    var dateDay=""

    var isSelected=false
}