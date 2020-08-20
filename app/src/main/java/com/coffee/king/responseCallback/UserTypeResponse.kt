package com.coffee.king.responseCallback

import com.coffee.king.model.ModelEventType
import com.coffee.king.model.ModelUserType
import com.google.gson.annotations.SerializedName

class UserTypeResponse {

    @SerializedName("user_type")
    var arrOfUserType=ArrayList<ModelUserType>()
}