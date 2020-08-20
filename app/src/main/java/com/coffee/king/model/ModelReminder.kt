package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelReminder : Serializable {

    @SerializedName("reminder_id")
    var reminderId=""

    @SerializedName("r_date")
    var reminderDate=""

    @SerializedName("r_time")
    var reminderTime=""

    @SerializedName("r_responsible")
    var responsibleId=""

    @SerializedName("r_notes")
    var reminderNotes=""

    @SerializedName("responsible_name")
    var responsibleName=""

    @SerializedName("user_type")
    var responsibleType=""

    @SerializedName("user_id")
    var userId=""

}