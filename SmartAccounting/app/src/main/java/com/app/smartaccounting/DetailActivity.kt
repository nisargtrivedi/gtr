package com.app.smartaccounting

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartaccounting.adapter.ChatAdapter
import com.app.smartaccounting.adapter.FilesAdapter
import com.app.smartaccounting.databinding.ChatBinding
import com.app.smartaccounting.databinding.DetailBinding
import com.app.smartaccounting.databinding.ForgotPasswordBinding
import com.app.smartaccounting.databinding.SignupBinding
import com.app.smartaccounting.databinding.UpdateProfileBinding
import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.util.URIPathHelper
import com.app.smartaccounting.util.Util
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import kotlin.coroutines.CoroutineContext

class DetailActivity : BaseActivity() , CoroutineScope{
    private lateinit var binding: DetailBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    lateinit var chatAdapter: FilesAdapter
    lateinit var images: ReceivedMessage
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        binding = DataBindingUtil.setContentView(this,R.layout.detail)
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        images= intent.getSerializableExtra("images") as ReceivedMessage
        chatAdapter = FilesAdapter(this,images.filesList)
        binding.rvImages.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvImages.adapter = chatAdapter



        appPreferences= AppPreferences(this)
        try {
            binding.tvBody.text = images.MessageBody
            binding.tvTitle.text = images.MessageTitle
            binding.imgBack.setOnClickListener {
                finish()
            }

            CoroutineScope(job).launch {
                withContext(Dispatchers.Main){
                    readMessage()
                }
            }
        }catch (e:Exception){
        }
    }

    fun readMessage(){
        if (isNetworkAvailable) {
            val params = JSONObject()
            params.put("user_id", appPreferences.user.userID)
            params.put("message_id",images.ReceivedMessageID)

            viewModel.ChangePassword("Bearer "+appPreferences.user.auth_key,
                Gson().fromJson(
                    params.toString(),
                    JsonObject::class.java
                ),4).observe(this) {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading(this)
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        showToast(it.message)
                    }
                    Status.ERROR -> {
                        hideLoading()
                        showToast(it.message)
                    }
                    else -> {
                        hideLoading()
                    }
                }

            }
        }
        else {
            showToast(getString(R.string.no_net))
        }

    }

}