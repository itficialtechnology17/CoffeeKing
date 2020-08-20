package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCafeLocation
import com.coffee.king.model.ModelReminder
import com.google.gson.annotations.SerializedName

class ReminderResponse {

    @SerializedName("data")
    var arrOfReminder=ArrayList<ModelReminder>()
}