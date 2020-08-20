package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCafeLocation
import com.google.gson.annotations.SerializedName

class CafeLocationResponse {

    @SerializedName("cafe_location")
    var arrOfCafeLocation=ArrayList<ModelCafeLocation>()
}