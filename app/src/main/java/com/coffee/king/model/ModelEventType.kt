package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelEventType : Serializable {

    @SerializedName("eventId")
    var eventId=""

    @SerializedName("eventName")
    var eventName=""

    var isSelected=false
}