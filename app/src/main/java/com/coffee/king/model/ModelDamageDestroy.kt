package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelDamageDestroy : Serializable {

    @SerializedName("damage_destroy_id")
    var damageDestroyId=""

    @SerializedName("item_name")
    var itemName=""

    @SerializedName("reason")
    var reason=""

    @SerializedName("by_whom_name")
    var byWhomName=""

    @SerializedName("by_whom_id")
    var byWhomId=""

    @SerializedName("contact_number")
    var contactNumber=""

    @SerializedName("in_charge_id")
    var inChargeId=""

    @SerializedName("users")
    var userName=""

    @SerializedName("d_d_timestamp")
    var timestamp=""

}