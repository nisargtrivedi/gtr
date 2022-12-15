package com.app.smartaccounting.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.app.smartaccounting.app.Accounting
import com.app.smartaccounting.repo.userRepo
import com.app.smartaccounting.util.Resource
import com.app.smartaccounting.util.Util
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody
import okhttp3.RequestBody

class userViewModel(private val userRepo: userRepo) : ViewModel() {

    fun LoginorRegistration(
        jsonObject: JsonObject,i:Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.userLogin(jsonObject,i)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }


    fun ChangePassword(token:String,
        jsonObject: JsonObject,i:Int
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.changePassword(token,jsonObject,i)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }



    fun chatAPI(
        token:String,
        jsonObject: JsonObject,i:String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.chatAPI(token,jsonObject,i)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Util.startLogin(Accounting.appContext)
        }
    }

    fun loadchatAPI(
        token:String,
        page: String,size:Int,username:String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.loadchatAPI(token,page,size,username)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Util.startLogin(Accounting.appContext)
        }
    }


    fun loadDocuments(
        token:String,
        page: JsonObject
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.loadMessages(token,page)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Util.startLogin(Accounting.appContext)
        }
    }


    fun loadReceivedAPI(
        token:String,
        page: Int,size:Int,username:String,isSent:Int,catID:String,date:String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.loadReceivedchatAPI(token,page,size,username,isSent,catID,date)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Util.startLogin(Accounting.appContext)

        }
    }


    fun loadCategory(
        token:String,
       username:String
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.loadCategory(token,username,"ar")))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Util.startLogin(Accounting.appContext)
        }
    }




    fun CMSMethod(
        id:Int,
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.CMSContent(id)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            Util.startLogin(Accounting.appContext)
        }
    }


    /*fun sendImages(
        token:String,
        map: HashMap<String,String>
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.sendImages(token,map)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }*/

    fun sendImages(
        token:String,
        userID:RequestBody,title:RequestBody,body:RequestBody,category_id:RequestBody,pic: List<MultipartBody.Part>
    ) = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.sendImages(token,userID,title,body,category_id,pic)))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
            //Util.startLogin(Accounting.appContext)
        }
    }





     fun updateProfile(session: String,
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
    )= liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(data = userRepo.updateProfile(session,id,username,phone,device_type,device_model,os_version,
                app_version,device_token,image)))
        } catch (exception: Exception) {
            Log.e( "Error Occurred!------------",exception.toString())
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
           // Util.startLogin(Accounting.appContext)
        }
    }
}