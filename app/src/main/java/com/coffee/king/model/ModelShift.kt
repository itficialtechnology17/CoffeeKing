package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelShift : Serializable {

    @SerializedName("shift_id")
    var shiftId=""

    @SerializedName("shift_name")
    var shiftName=""

    @SerializedName("shift_start_time")
    var shiftStartTime=""

    @SerializedName("shift_end_time")
    var shiftEndTime=""

    @SerializedName("shift_status")
    var status=""


    var isSelected=false
}
