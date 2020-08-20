package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelDuty : Serializable {

    @SerializedName("duty_id")
    var dutyId=""

    @SerializedName("duty_title")
    var dutyTitle=""

    var isSelected=false
}