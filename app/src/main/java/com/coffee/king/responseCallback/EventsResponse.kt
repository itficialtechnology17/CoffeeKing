package com.coffee.king.responseCallback

import com.coffee.king.model.ModelEventType
import com.google.gson.annotations.SerializedName

class EventsResponse {

    @SerializedName("events")
    var arrOfModelEventType=ArrayList<ModelEventType>()
}