package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelUpgradeEvent : Serializable {

    @SerializedName("upgradeEventId")
    var upgradeEventId=""

    @SerializedName("upgradeEventName")
    var upgradeEventName=""

    @SerializedName("upgradeEventPrice")
    var upgradeEventPrice=""

    var isSelected=false

}