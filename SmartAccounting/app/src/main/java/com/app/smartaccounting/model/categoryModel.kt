package com.app.smartaccounting.model

import com.google.gson.annotations.SerializedName

class categoryModel : java.io.Serializable {

    @SerializedName("id")
    var ID : String = "0"


    @SerializedName("name")
    var Name : String = ""

    @SerializedName("icon")
    var icon : String = ""

    @SerializedName("unread_messages")
    var unread_messages : String = ""

    var isSelected = 0

   }