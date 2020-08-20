package com.coffee.king.responseCallback

import com.google.gson.annotations.SerializedName

class LoginResponse {

    @SerializedName("code")
    var responseCode = 1000

    @SerializedName("currentDate")
    var currentDate = ""

    @SerializedName("message")
    var responseMessage = ""

    @SerializedName("loginId")
    var loginId = ""

    @SerializedName("userType")
    var userType = ""

    @SerializedName("userName")
    var userName = ""

}