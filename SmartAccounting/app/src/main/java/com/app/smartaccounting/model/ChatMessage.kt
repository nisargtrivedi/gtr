package com.app.smartaccounting.model

import com.google.gson.annotations.SerializedName

class ChatMessage : java.io.Serializable {

    @SerializedName("id")
    var chatID : String = "0"

    @SerializedName("body")
    var chatMessage : String = ""

    @SerializedName("send_by_user")
    var chatUser : Boolean = false

    @SerializedName("date")
    var chattTime : String = ""

}