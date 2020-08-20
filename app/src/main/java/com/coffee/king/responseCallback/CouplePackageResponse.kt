package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCouplePackage
import com.coffee.king.model.ModelUpgradeEvent
import com.google.gson.annotations.SerializedName

class CouplePackageResponse {

    @SerializedName("couple_package")
    var arrOfCouplePackage = ArrayList<ModelCouplePackage>()

}