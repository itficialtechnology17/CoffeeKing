package com.coffee.king.responseCallback

import com.coffee.king.model.ModelEventType
import com.coffee.king.model.ModelPackage
import com.google.gson.annotations.SerializedName

class PackageResponse {

    @SerializedName("package")
    var arrOfPackage=ArrayList<ModelPackage>()
}