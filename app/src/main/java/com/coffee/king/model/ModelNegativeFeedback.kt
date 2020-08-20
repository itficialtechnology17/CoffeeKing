package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelNegativeFeedback : Serializable {

    @SerializedName("negative_feedback_id")
    var negativeFeedbackId=""

    @SerializedName("issue")
    var issue=""

    @SerializedName("solution")
    var solution=""

    @SerializedName("r_responsible_id")
    var responsibleId=""

    @SerializedName("responsible_name")
    var responsibleName=""

    @SerializedName("added_date")
    var addedDate=""


    @SerializedName("user_id")
    var userId=""

}