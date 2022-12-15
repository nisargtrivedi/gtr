package com.app.smartaccounting.repo

import com.app.smartaccounting.network.APIInterface
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Header
import retrofit2.http.Part
import retrofit2.http.Query

class userRepo(private val apiInterface: APIInterface)  {
    suspend fun userLogin(username: JsonObject,i:Int) = apiInterface.loginAPI(username,if(i==1)"login" else if(i==2) "register" else if(i==3) "forgot-password" else "update-password")

    suspend fun changePassword(token:String,username: JsonObject,i:Int) = apiInterface.loginAPI(token,username,if(i==1)"update-password" else if(i==2)"deactivate-user" else if(i==3) "logout" else "read-message")


    suspend fun updateProfile(session: String,
                               id: String,
                               username: String,
                              phone: String,
                               device_type: String,
                               device_model: String,
                               os_version: String,
                               app_version: String,
                               device_token: String,
                              image: String
                               //pic: MultipartBody.Part
    ) = apiInterface.updateProfile(session,id,username,phone,device_type,device_model,os_version,app_version,device_token,image)

    suspend fun chatAPI(token:String,username: JsonObject,param:String) = apiInterface.chatAPI(token,username,param)

    suspend fun loadchatAPI(token:String, page:String,
                            pageSize:Int,
                            userId:String) = apiInterface.loadChat(token,page,pageSize,userId)

    suspend fun loadReceivedchatAPI(token:String, page:Int,
                            pageSize:Int,
                            userId:String,is_sent:Int,catID:String,date:String) = if(catID=="0")apiInterface.loadReceivedMessage(token,page,pageSize,userId,is_sent,date) else apiInterface.loadReceivedMessage(token,page,pageSize,userId,is_sent,catID,date)

    suspend fun loadCategory(token:String,
                                    userId:String,lang:String) = apiInterface.loadCategory(token,userId)

    suspend fun CMSContent(id:Int) = apiInterface.CMS(id)


    //suspend fun sendImages(token:String,hashMap: HashMap<String,String>) = apiInterface.sendImages(token,hashMap)

    suspend fun sendImages(token:String,userID:RequestBody,title:RequestBody,body:RequestBody,category_id:RequestBody,pic: List<MultipartBody.Part>) =
        apiInterface.sendImages(token,userID,title,body,category_id, images = pic)


    suspend fun loadMessages(token:String,username: JsonObject) = apiInterface.messageAPI(token,username,"search")



}