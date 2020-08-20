package com.coffee.king.responseCallback

import com.google.gson.annotations.SerializedName

class ResponseStatusCode {

    @SerializedName("code")
    var statusCode=0

    @SerializedName("message")
    var message=""

    @SerializedName("id")
    var id=""
}