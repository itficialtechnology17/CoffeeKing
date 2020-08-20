package com.coffee.king.responseCallback

import com.coffee.king.model.*
import com.google.gson.annotations.SerializedName

class UserResponse {

    @SerializedName("user")
    var arrOfUser=ArrayList<ModelUser>()
}