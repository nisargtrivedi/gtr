package com.app.smartaccounting

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.smartaccounting.adapter.CategoryAdapter
import com.app.smartaccounting.adapter.ReceivedAdapter
import com.app.smartaccounting.databinding.ReceivedBinding
import com.app.smartaccounting.listeners.onCategoryClick
import com.app.smartaccounting.listeners.onFileClick
import com.app.smartaccounting.model.ReceivedMessage
import com.app.smartaccounting.model.categoryModel
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext

class ReceivedActivity : BaseActivity() , CoroutineScope, onCategoryClick,onFileClick {
    private lateinit var binding: ReceivedBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    lateinit var chatAdapter: ReceivedAdapter
    lateinit var categoryAdapter: CategoryAdapter
    var name:String=""
    private var isLoading: Boolean = false

    var chatList = mutableListOf<ReceivedMessage>()
    var categoryList = mutableListOf<categoryModel>()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    private lateinit var layoutManager : LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        name=intent.getStringExtra("name") as String
        binding = DataBindingUtil.setContentView(this,R.layout.received)
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        layoutManager =  LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)

        chatAdapter = ReceivedAdapter(this,chatList)
        binding.rvMessage.layoutManager = layoutManager
        binding.rvMessage.adapter = chatAdapter
        chatAdapter.onFile(this)


        categoryAdapter = CategoryAdapter(this,categoryList)
        binding.rvCategory.layoutManager = LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false)
        binding.rvCategory.adapter = categoryAdapter
        categoryAdapter.onCategoryClickListeners(this)



        appPreferences= AppPreferences(this)
        try {
            //load Chat
            if(name=="two")binding.imgSendDocument.visibility=View.INVISIBLE else binding.imgSendDocument.visibility = View.VISIBLE

            binding.imgBack.setOnClickListener {
                finish()
            }
            binding.imgSendDocument.setOnClickListener {
                startActivity(Intent(this,SendDocumentActivity::class.java))
            }

            binding.rvMessage.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    if (!isLoading) {
                        if (layoutManager.findLastCompletelyVisibleItemPosition() == chatList.size - 1) {
                            if (chatList.size > 0) {
                                if(chatList.size>20) {
                                    isLoading = true
                                    //loadMain(chatList.get(chatList.size - 1).chattTime)
                                }
                            }
                        }
                    }
                }
            })

        }catch (e:Exception){
        }
    }

    override fun onResume() {
        super.onResume()
        loadCategory()
    }

    fun loadCategory(){
        //load Chat
        CoroutineScope(Dispatchers.Main).launch {

            viewModel.loadCategory("Bearer " + appPreferences.user.auth_key,appPreferences.user.userID).observe(this@ReceivedActivity,
                androidx.lifecycle.Observer {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@ReceivedActivity)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                categoryList.clear()
                                categoryList.addAll(it.data?.chatMessage!!.messageList)
                                categoryAdapter.notifyDataSetChanged()
                                loadMain("0")
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


    fun loadMain(catID:String,date:String=""){
        CoroutineScope(Dispatchers.Main).launch {

            viewModel.loadReceivedAPI("Bearer " + appPreferences.user.auth_key,1,500,appPreferences.user.userID,if(name=="two") 0 else 1,catID,date).observe(this@ReceivedActivity,
                androidx.lifecycle.Observer  {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@ReceivedActivity)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                chatList.clear()
                                chatList.addAll(it.data?.chatMessage!!.messageList)
                                chatAdapter.notifyDataSetChanged()
                                if(chatList.size>0){
                                    binding.rvMessage.visibility = View.VISIBLE
                                    binding.tvMsg.visibility = View.GONE
                                }else{
                                    binding.rvMessage.visibility = View.GONE
                                    binding.tvMsg.visibility = View.VISIBLE
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

    override fun onClick(model: categoryModel) {
        for(i in categoryList)i.isSelected=0
        model.isSelected=1
        categoryAdapter.notifyDataSetChanged()
        loadMain(model.ID)
    }

    override fun onClick(model: ReceivedMessage) {
        startActivity(Intent(this,DetailActivity::class.java).putExtra("images",model))
    }

}