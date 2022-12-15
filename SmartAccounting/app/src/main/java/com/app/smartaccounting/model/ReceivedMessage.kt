package com.app.smartaccounting.model

import com.google.gson.annotations.SerializedName

class ReceivedMessage : java.io.Serializable {

    @SerializedName("id")
    var ReceivedMessageID : String = "0"

    @SerializedName("body")
    var MessageBody : String = ""

    @SerializedName("title")
    var MessageTitle : String = ""

    @SerializedName("send_by_user")
    var chatUser : Boolean = false

    @SerializedName("date")
    var chattTime : String = ""

    @SerializedName("is_read")
    var isRead= "0"

    @SerializedName("category_id")
    var categoryID= "0"

    @SerializedName("category_name")
    var categoryName= ""


    @SerializedName("files")
    var filesList:List<imageFiles> = mutableListOf()




    inner class imageFiles : java.io.Serializable
    {
        @SerializedName("id")
        var imageID:String = "0"

        @SerializedName("image")
        var imagePath:String=""

        @SerializedName("remark")
        var imageremark:String=""

    }


    /*"id": "160",
    "title": "Test",
    "body": "test",
    "category_id": "14",
    "category_name": "المحاسبة",
    "sender_id": "1",
    "sender_name": "Dhaval T Soni",
    "receiver_name": null,
    "receiver_id": "84",
    "send_by_user": false,
    "date": "2022-11-12 16:20:33",
    "files": [
    {
        "id": "337",
        "image": "/web/uploads/1668270022636fc7c6c3ee95.01926054.png",
        "remark": "test"
    }
    ]*/

}