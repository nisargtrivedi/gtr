package com.app.smartaccounting.parser

import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.userModel
import com.app.smartaccounting.viewmodels.BaseModel
import com.google.gson.annotations.SerializedName

class cmsparser : BaseModel() {

    @SerializedName("data")
    lateinit var content:CMSContent

    class CMSContent{

        @SerializedName("page")
        var page:String = ""

        @SerializedName("content")
        var content:String = ""


    }
}