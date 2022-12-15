package com.app.smartaccounting.model

import com.google.gson.annotations.SerializedName

class userModel : java.io.Serializable {

    @SerializedName("id")
    var userID : String = "0"


    @SerializedName("user_name")
    var userName : String = ""

    @SerializedName("email")
    var email : String = ""

    @SerializedName("phone")
    var phone : String = ""

    @SerializedName("profile_image")
    var profile_image : String = ""

    @SerializedName("auth_key")
    var auth_key : String = ""



    /*"id": "80",
    "is_active": "",
    "user_name": "",
    "email": "nisarg@codeflixweb.com",
    "phone": "9978654321",
    "profile_image": "",
    "device_token": "",
    "device_type": "",
    "device_model": "",
    "app_version": "",
    "os_version": "",
    "auth_key": "IASDmS-DHx7uXOlH8KU2WelB9ZNgUedD"*/
}