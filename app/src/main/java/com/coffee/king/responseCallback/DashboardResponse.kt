package com.coffee.king.responseCallback

import com.coffee.king.model.ModelEventByDate
import com.google.gson.annotations.SerializedName

class DashboardResponse {

    @SerializedName("advance_payment")
    var arrOfAdvancePayment=ArrayList<ModelEventByDate>()

    @SerializedName("upcoming_event")
    var arrOfUpcomingEvent=ArrayList<ModelEventByDate>()

    @SerializedName("past_event")
    var arrOfPastEvent=ArrayList<ModelEventByDate>()

    @SerializedName("enquiry")
    var arrOfEnquiry=ArrayList<ModelEventByDate>()
}