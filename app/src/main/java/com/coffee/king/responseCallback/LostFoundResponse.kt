package com.coffee.king.responseCallback

import com.coffee.king.model.ModelLostFound
import com.google.gson.annotations.SerializedName

class LostFoundResponse {

    @SerializedName("data")
    var arrOfLostFound=ArrayList<ModelLostFound>()
}