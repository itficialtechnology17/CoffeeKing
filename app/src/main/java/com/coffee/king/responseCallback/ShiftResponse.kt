package com.coffee.king.responseCallback

import com.coffee.king.model.ModelShift
import com.google.gson.annotations.SerializedName

class ShiftResponse {

    @SerializedName("data")
    var arrOfShift = ArrayList<ModelShift>()
}