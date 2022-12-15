package com.app.smartaccounting.parser

import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.model.userModel
import com.app.smartaccounting.viewmodels.BaseModel
import com.google.gson.annotations.SerializedName

class receivedParser : BaseModel() {

    @SerializedName("data")
    lateinit var chatMessage:MessageList

    class MessageList{

        @SerializedName("messages")
        var messageList:List<ReceivedMessage> = mutableListOf()
    }
}