package com.coffee.king.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class ModelNotes : Serializable {

    @SerializedName("note_id")
    var noteId=""

    @SerializedName("note_title")
    var noteTitle=""

    @SerializedName("note_desc")
    var noteDesc=""

    @SerializedName("user_id")
    var userId=""
}