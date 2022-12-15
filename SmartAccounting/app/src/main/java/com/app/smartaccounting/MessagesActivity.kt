package com.app.smartaccounting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.smartaccounting.adapter.ReceivedAdapterTwo
import com.app.smartaccounting.databinding.MessageActivityBinding
import com.app.smartaccounting.listeners.onFileClick
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class MessagesActivity : BaseActivity(), CoroutineScope, onFileClick {
    private lateinit var binding: MessageActivityBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    var filesList = mutableListOf<ReceivedMessage>()
    lateinit var receivedAdapterTwo: ReceivedAdapterTwo


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        appPreferences = AppPreferences(this)
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        binding = MessageActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        CoroutineScope(Dispatchers.Main).launch {
            receivedAdapterTwo = ReceivedAdapterTwo(this@MessagesActivity,filesList)
            binding.rvMessage.layoutManager = LinearLayoutManager(this@MessagesActivity)
            binding.rvMessage.adapter = receivedAdapterTwo
            receivedAdapterTwo.onFile(this@MessagesActivity)
                loadMessage()
        }
        binding.imgBack.setOnClickListener { finish() }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
            IntentFilter("chat")
        );


     }
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            Log.d("receiver", "Got message: $message")
            if(message.equals("file")) loadMessage()
        }
    }
    fun loadMessage(){
        val param = JSONObject()
        param.put("user_id",appPreferences.user.userID)
        if(intent.extras!=null) {
            if(intent.hasExtra("category")){
                param.put("category_id",intent.getStringExtra("category"))
            }else if(intent.hasExtra("search")){
                param.put("title",intent.getStringExtra("search"))
            }
        }
        else{
            param.put("title","")
            param.put("category_id","")
        }

        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadDocuments("Bearer " + appPreferences.user.auth_key,Gson().fromJson(
                param.toString(),
                JsonObject::class.java
            )).observe(this@MessagesActivity,
                androidx.lifecycle.Observer {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@MessagesActivity)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                filesList.clear()
                                it.data?.chatMessage?.messageList?.let { it1->
                                    filesList.addAll(it1)
                                }
                                receivedAdapterTwo.notifyDataSetChanged()
                                if(filesList.size>0) {
                                    binding.rvMessage.visibility=View.VISIBLE
                                    binding.tvNoDocument.visibility=View.GONE
                                }else{
                                    binding.rvMessage.visibility=View.GONE
                                    binding.tvNoDocument.visibility=View.VISIBLE
                                }
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
                })
        }
    }



    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onClick(model: ReceivedMessage) {
        if(model.filesList.size>0) startActivity(Intent(this,DetailActivity::class.java).putExtra("images",model))
    }
}