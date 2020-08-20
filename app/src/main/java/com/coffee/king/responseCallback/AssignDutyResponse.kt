package com.coffee.king.responseCallback

import com.coffee.king.model.ModelAssignDuty
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class AssignDutyResponse : Serializable {

    @SerializedName("data")
    var arrOfAssignDuty=ArrayList<ModelAssignDuty>()
}