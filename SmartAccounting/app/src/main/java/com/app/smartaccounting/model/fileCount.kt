package com.app.smartaccounting.model

import com.google.gson.annotations.SerializedName

class fileCount : java.io.Serializable {

    @SerializedName("sent")
    var sentCount : String = "0"

    @SerializedName("received")
    var receiveCount : String = "0"

    @SerializedName("total")
    var totalCount : String = "0"

}