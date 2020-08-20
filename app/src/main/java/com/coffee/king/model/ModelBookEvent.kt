package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelBookEvent : Serializable {

    @SerializedName("date_of_event")
    var dateOfEvent=""

    @SerializedName("events_by_date")
    var arrOfEventByDate=ArrayList<ModelEventByDate>()

}