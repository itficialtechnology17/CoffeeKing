package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import android.util.Patterns



class ModelUser : Serializable {

    @SerializedName("userId")
    var userId=""

    @SerializedName("userPassword")
    var userPassword=""

    @SerializedName("userName")
    var userName=""

    @SerializedName("userMobile")
    var userMobile=""

    @SerializedName("userTypeId")
    var userTypeId=""

    @SerializedName("userTypeName")
    var userTypeName=""

    @SerializedName("locationId")
    var locationId=""

    @SerializedName("locationName")
    var locationName=""

    @SerializedName("isActive")
    var isActive=1

    @SerializedName("isSelected")
    var isSelected=false
}