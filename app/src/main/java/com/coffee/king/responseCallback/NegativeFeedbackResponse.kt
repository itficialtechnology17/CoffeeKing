package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCafeLocation
import com.coffee.king.model.ModelNegativeFeedback
import com.coffee.king.model.ModelReminder
import com.google.gson.annotations.SerializedName

class NegativeFeedbackResponse {

    @SerializedName("data")
    var arrOfNegativeFeedback=ArrayList<ModelNegativeFeedback>()
}