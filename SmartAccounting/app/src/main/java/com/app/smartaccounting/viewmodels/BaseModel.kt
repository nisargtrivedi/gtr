package com.app.smartaccounting.viewmodels

import com.google.gson.annotations.SerializedName

open class BaseModel {
    @SerializedName("success")
    var success:Boolean =  false

    @SerializedName("status")
    var status:Int =  200

    @SerializedName("message")
    var message:String =  ""
}

/*
{
    "success": false,
    "status": 403,
    "message": "User request declined",
    "data": {}
}*/
