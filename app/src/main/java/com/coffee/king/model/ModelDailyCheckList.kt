package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelDailyCheckList : Serializable{

    @SerializedName("checklist_master_id")
    var checkListMId=""

    @SerializedName("checklist_type")
    var checkListType=""

    @SerializedName("title_time")
    var titleTime=""

    @SerializedName("title_text")
    var titleText=""

    var isSelected=false
}