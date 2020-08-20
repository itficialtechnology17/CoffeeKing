package com.coffee.king.responseCallback

import com.coffee.king.model.ModelDamageDestroy
import com.coffee.king.model.ModelDuty
import com.coffee.king.model.ModelLostFound
import com.coffee.king.model.ModelShift
import com.google.gson.annotations.SerializedName

class DutyResponse {
    @SerializedName("data")
    var arrOfDuty=ArrayList<ModelDuty>()
}