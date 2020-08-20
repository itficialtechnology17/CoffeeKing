package com.coffee.king.responseCallback

import com.coffee.king.model.ModelCheckList
import com.google.gson.annotations.SerializedName

class CheckListResponse {

    @SerializedName("check_list")
    var arrOfCheckList=ArrayList<ModelCheckList>()
}