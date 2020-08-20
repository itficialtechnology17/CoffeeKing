package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelCouplePackage : Serializable {

    @SerializedName("couplePackageId")
    var couplePackageId=""

    @SerializedName("packageTitle")
    var packageTitle=""

    @SerializedName("packagePrice")
    var packagePrice=""

    @SerializedName("packageType")
    var packageType=""


    var isSelected=false

}