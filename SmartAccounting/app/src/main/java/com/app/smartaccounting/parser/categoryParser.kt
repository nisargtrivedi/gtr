package com.app.smartaccounting.parser

import com.app.smartaccounting.model.*
import com.app.smartaccounting.viewmodels.BaseModel
import com.google.gson.annotations.SerializedName

class categoryParser : BaseModel() {

    @SerializedName("data")
    lateinit var chatMessage:MessageList

    class MessageList{

        @SerializedName("categories")
        var messageList:List<categoryModel> = mutableListOf()

        @SerializedName("files_count")
        var fileCount:List<fileCount> = mutableListOf()

        @SerializedName("files")
        var recentFiles:List<ReceivedMessage> = mutableListOf()
    }
}