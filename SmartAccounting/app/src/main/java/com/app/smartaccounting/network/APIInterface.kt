package com.app.smartaccounting.network


//import com.dsm.ui.model.BaseModel
//import com.dsm.ui.parser.*
import com.app.smartaccounting.parser.*
import com.app.smartaccounting.viewmodels.BaseModel
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface APIInterface {


    @POST("{name}")
    suspend fun loginAPI(
        @Body  params: JsonObject,@Path("name") name:String
    ) : loginparser


    @POST("{name}")
    suspend fun loginAPI(
        @Header("Authorization") session: String,
        @Body  params: JsonObject,
        @Path("name") name:String
    ) : loginparser


    @POST("{name}")
    suspend fun messageAPI(
        @Header("Authorization") session: String,
        @Body  params: JsonObject,@Path("name") name:String
    ) : receivedParser


    @FormUrlEncoded
    @PUT("edit-profile-android")
    suspend fun updateProfile(
        @Header("Authorization") session: String,
        @Field("user_id") id: String,
        @Field("user_name") username: String,
        @Field("phone") phone: String,
        @Field("device_type") device_type: String,
        @Field("device_model") device_model: String,
        @Field("os_version") os_version: String,
        @Field("app_version") app_version: String,
        @Field("device_token") device_token: String,
        @Field("profile_image") profile_image: String,
        //@Part pic: MultipartBody.Part
    ) : loginparser

    @POST("{name}")
    suspend fun chatAPI(
        @Header("Authorization") session: String,
        @Body  params: JsonObject,
        @Path("name") name:String
    ) : loginparser

    @GET("chat")
    suspend fun loadChat(
        @Header("Authorization") session: String,
        @Query("date") page:String,
        @Query("page_size") pageSize:Int,
        @Query("user_id") userId:String
        ) : messageParser

    @GET("send-message-list")
    suspend fun loadReceivedMessage(
        @Header("Authorization") session: String,
        @Query("page") page:Int,
        @Query("page_size") pageSize:Int,
        @Query("user_id") userId:String,
        @Query("is_sent") is_sent:Int,
        @Query("date") date:String,
        ) : receivedParser

    @GET("send-message-list")
    suspend fun loadReceivedMessage(
        @Header("Authorization") session: String,
        @Query("page") page:Int,
        @Query("page_size") pageSize:Int,
        @Query("user_id") userId:String,
        @Query("is_sent") is_sent:Int,
        @Query("category_id") cateID:String,
        @Query("date") date:String,
        ) : receivedParser


    @GET("home")
    suspend fun loadCategory(
        @Header("Authorization") session: String,
        @Query("user_id") userId:String
    ) : categoryParser

    /*@FormUrlEncoded
    @Multipart
    @POST("send-file-message-test")
    suspend fun sendImages(
        @Header("Authorization") session: String,

        @FieldMap map: HashMap<String,String>
        )
*/
    @Multipart
    @POST("send-file-message")
    suspend fun sendImages(
        @Header("Authorization") session: String,
        @Part("user_id") userID:RequestBody,
        @Part("title") title:RequestBody,
        @Part("body") body:RequestBody,
        @Part("category_id") category_id:RequestBody,
        @Part images: List<MultipartBody.Part>
    ) : loginparser


    @GET("cms")
    suspend fun CMS(
        @Query("id") id:Int
    ) : cmsparser


}