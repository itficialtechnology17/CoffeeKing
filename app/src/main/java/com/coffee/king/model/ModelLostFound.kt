package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelLostFound : Serializable {

    @SerializedName("lost_found_id")
    var lostFoundId=""

    @SerializedName("item_name")
    var itemName=""

    @SerializedName("name")
    var name=""

    @SerializedName("contact_number")
    var contactNumber=""

    @SerializedName("date_of_lost")
    var dateOfLost=""

    @SerializedName("time_of_lost")
    var timeOfOfLost=""

    @SerializedName("date_time_of_found")
    var foundTimeStamp=""

    @SerializedName("claim_by")
    var claimBy=""


    @SerializedName("status")
    var status=""
}