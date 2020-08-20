package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelShiftDate : Serializable {

    @SerializedName("shiftDate")
    var shiftDate=""

    @SerializedName("shiftServerDate")
    var shiftServerDate=""

    @SerializedName("shiftId")
    var shiftId=""

    @SerializedName("shiftName")
    var shiftName=""

    @SerializedName("shiftStartTime")
    var shiftStartTime=""

    @SerializedName("shiftEndTime")
    var shiftEndTime=""

    var isSelected=false

}