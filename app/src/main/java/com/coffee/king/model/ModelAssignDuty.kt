package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelAssignDuty : Serializable {

    @SerializedName("duty_chart_id")
    var assignDutyChartId=""

    @SerializedName("duty_id")
    var assignDutyId=""

    @SerializedName("dutiy_title")
    var assignDutyText=""

    @SerializedName("has_multiple_checklist")
    var assignDutyHasMultipleCheckList=""

    @SerializedName("check_list_type")
    var assignDutyType=""

}