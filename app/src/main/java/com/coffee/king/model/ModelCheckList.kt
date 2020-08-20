package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelCheckList : Serializable {

    @SerializedName("check_list_master_id")
    var checkedListId=""

    @SerializedName("check_list_title")
    var checkedListTitle=""

    @SerializedName("checked")
    var isChecked="0"

    @SerializedName("reason")
    var reason=""

    var isSelected=false
}