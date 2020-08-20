package com.coffee.king.responseCallback

import com.coffee.king.model.ModelUpgradeEvent
import com.google.gson.annotations.SerializedName

class UpgradeEventResponse {

    @SerializedName("upgrade_event")
    var arrOfUpgradeEvent = ArrayList<ModelUpgradeEvent>()

}