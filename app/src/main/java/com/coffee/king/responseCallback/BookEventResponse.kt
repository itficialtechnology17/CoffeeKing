package com.coffee.king.responseCallback

import com.coffee.king.model.*
import com.google.gson.annotations.SerializedName

class BookEventResponse {

    @SerializedName("events")
    var arrOfBookEvent=ArrayList<ModelBookEvent>()
}