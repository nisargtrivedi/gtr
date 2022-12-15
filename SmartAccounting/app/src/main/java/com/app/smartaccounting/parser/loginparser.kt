package com.app.smartaccounting.parser

import com.app.smartaccounting.model.userModel
import com.app.smartaccounting.viewmodels.BaseModel
import com.google.gson.annotations.SerializedName

class loginparser : BaseModel() {

    @SerializedName("data")
    lateinit var userData:userModel
}