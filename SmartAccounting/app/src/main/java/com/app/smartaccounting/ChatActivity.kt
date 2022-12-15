package com.app.smartaccounting

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.adapter.ChatAdapter
import com.app.smartaccounting.databinding.ChatBinding
import com.app.smartaccounting.model.ChatMessage
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.CoroutineContext

class ChatActivity : BaseActivity() , CoroutineScope{
    private lateinit var binding: ChatBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    lateinit var chatAdapter: ChatAdapter
    var chatList = mutableListOf<ChatMessage>()
    private var isLoading: Boolean = false
    var editable= true

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private lateinit var layoutManager : LinearLayoutManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        binding = DataBindingUtil.setContentView(this,R.layout.chat)
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        chatAdapter = ChatAdapter(this,chatList)
        layoutManager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        binding.rvMessage.layoutManager = layoutManager
        binding.rvMessage.adapter = chatAdapter

        appPreferences= AppPreferences(this)
        try {
            if(!appPreferences.user.profile_image.isEmpty()) Glide.with(this).load(RetrofitBuilder.BASE_URL_IMAGE+appPreferences.user.profile_image).into(binding.imgUser)

            binding.tvUsername.text = appPreferences.user.userName+""


            binding.edtMessage.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    editable=false
                    binding.rvMessage.scrollToPosition(chatList.size - 1)
                }
            }
            binding.rvMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)


                        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {

                            if (!isLoading) {
                       //     if (chatList.size > 0) {
                           //     if(chatList.size>500) {
                                    if (editable) {
                                        isLoading = true
                                        loadChat(chatList.get(0).chattTime)
                                    }

                               // }
                            //}
                        }
                    }
                }
            })


            binding.btnSend.setOnClickListener {
                if(!TextUtils.isEmpty(binding.edtMessage.text.toString())){
                    editable = true
                    binding.edtMessage.clearFocus()
                    CoroutineScope(Dispatchers.Main).launch {
                        val param = JSONObject()
                        param.put("user_id",appPreferences.user.userID)
                        param.put("body",binding.edtMessage.text.toString())
                        viewModel.chatAPI("Bearer " + appPreferences.user.auth_key,  Gson().fromJson(
                            param.toString(),
                            JsonObject::class.java
                        ),"send-message").observe(this@ChatActivity,
                            androidx.lifecycle.Observer {
                                it.let {
                                    when (it.status) {
                                        Status.LOADING -> {
                                            //showLoading(this@ChatActivity)
                                        }
                                        Status.SUCCESS -> {
                                            //hideLoading()
                                            var obj = ChatMessage()
                                            obj.chatID="0"
                                            obj.chatMessage =binding.edtMessage.text.toString()
                                            obj.chatUser=true
                                            obj.chattTime=Date().time.toString()
                                            chatList.add(obj)
                                            binding.rvMessage.scrollToPosition(chatList.size - 1);
                                            chatAdapter.notifyDataSetChanged()
                                            binding.edtMessage.setText("")
                                        }
                                        Status.ERROR -> {
                                            //hideLoading()
                                              showToast(it.message)
                                        }
                                        else -> {
                                            //hideLoading()
                                        }
                                    }
                                }
                            })
                    }
                }else{
                    showToast("Please enter a message")
                }
            }

            loadChat()

            binding.imgBack.setOnClickListener {
                finish()
            }

        }catch (e:Exception){
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
             IntentFilter("chat")
        );
    }
    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            // Get extra data included in the Intent
            val message = intent.getStringExtra("message")
            Log.d("receiver", "Got message: $message")
            if(message.equals("text")) {
                chatList.clear()
                loadChat()
            }
        }
    }
    fun loadChat(time:String="")
    {
        //load Chat
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadchatAPI("Bearer " + appPreferences.user.auth_key,time,1000,appPreferences.user.userID).observe(this@ChatActivity,
                androidx.lifecycle.Observer {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@ChatActivity)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                binding.edtMessage.setText("")
                               // chatList.clear()
                                if(it.data?.chatMessage?.messageList!!.isNotEmpty()) {
                                    isLoading = false
                                    it.data?.chatMessage?.let { it1 ->
                                        chatList.addAll(it1.messageList)
                                        if(time=="") binding.rvMessage.scrollToPosition(chatList.size - 1);
                                    }

                                    chatAdapter.notifyDataSetChanged()
                                }else   isLoading = true
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
}