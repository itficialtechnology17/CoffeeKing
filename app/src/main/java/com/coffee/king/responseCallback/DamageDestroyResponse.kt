package com.coffee.king.responseCallback

import com.coffee.king.model.ModelDamageDestroy
import com.coffee.king.model.ModelLostFound
import com.google.gson.annotations.SerializedName

class DamageDestroyResponse {

    @SerializedName("data")
    var arrOfDamageDestroy=ArrayList<ModelDamageDestroy>()
}