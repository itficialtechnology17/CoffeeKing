package com.coffee.king.responseCallback

import com.coffee.king.model.ModelAssignDuty
import com.coffee.king.model.ModelDailyCheckList
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DailyCheckListResponse : Serializable {

    @SerializedName("data")
    var arrOfDailyCheckList=ArrayList<ModelDailyCheckList>()
}