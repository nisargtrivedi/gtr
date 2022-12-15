package com.app.smartaccounting

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.app.smartaccounting.adapter.HomeCategoryAdapter
import com.app.smartaccounting.adapter.HomeFilesAdapter
import com.app.smartaccounting.databinding.ActivityHomeBinding
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
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext


class Home : BaseActivity(), CoroutineScope,onCategoryClick, onFileClick {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    lateinit var homeCategoryAdapter: HomeCategoryAdapter
    lateinit var homeFilesAdapter: HomeFilesAdapter
    var categoryList = mutableListOf<categoryModel>()
    var filesList = mutableListOf<ReceivedMessage>()


    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        appPreferences = AppPreferences(this)
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.appBarHome.btnNewHome.setOnClickListener {
            binding.drawerLayout.openDrawer(Gravity.LEFT)
        }


        binding.rlOne.setOnClickListener {
            selectMenuColor(binding.rlOne,binding.rlTwo,binding.rlThree,binding.rlFour,binding.rlFive)
        }
        binding.rlTwo.setOnClickListener {
            selectMenuColor(binding.rlTwo,binding.rlOne,binding.rlThree,binding.rlFour,binding.rlFive)
            startActivity(Intent(this,UpdateProfileActivity::class.java))
        }
        binding.rlThree.setOnClickListener {
            selectMenuColor(binding.rlThree,binding.rlOne,binding.rlTwo,binding.rlFour,binding.rlFive)
        }
        binding.rlFour.setOnClickListener {
            selectMenuColor(binding.rlFour,binding.rlTwo,binding.rlThree,binding.rlOne,binding.rlFive)
            startActivity(Intent(this,MessagesActivity::class.java).putExtra("search","")
            )
        }
        binding.appBarHome.tvViewAll.setOnClickListener {
            startActivity(Intent(this,MessagesActivity::class.java).putExtra("search","")
            )
        }
        binding.rlFive.setOnClickListener {
            selectMenuColor(binding.rlFive,binding.rlTwo,binding.rlThree,binding.rlFour,binding.rlOne)
            logoutUser()
        }

        CoroutineScope(Dispatchers.Main).launch {
            binding.appBarHome.rvCategory.setHasFixedSize(true)
            binding.appBarHome.rvFiles.setHasFixedSize(true)

            homeCategoryAdapter = HomeCategoryAdapter(this@Home,categoryList)
            binding.appBarHome.rvCategory.layoutManager = GridLayoutManager(this@Home,4)
            binding.appBarHome.rvCategory.adapter = homeCategoryAdapter
            homeCategoryAdapter.onCategoryClickListeners(this@Home)

            homeFilesAdapter = HomeFilesAdapter(this@Home,filesList)
            binding.appBarHome.rvFiles.layoutManager = GridLayoutManager(this@Home,3)
            binding.appBarHome.rvFiles.adapter = homeFilesAdapter
            homeFilesAdapter.onCategoryClickListeners(this@Home)

        }
        binding.appBarHome.menuSettings.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java)
            )
        }
        binding.appBarHome.imgSettings.setOnClickListener {
/*
            startActivity(Intent(this,MessagesActivity::class.java).putExtra("search","")
            )
*/

            startActivity(Intent(this,ChatActivity::class.java))
        }
        binding.appBarHome.btnHome.setOnClickListener {
            startActivity(Intent(this,SendDocumentActivity::class.java))
        }
        binding.rlThree.setOnClickListener {
                startActivity(Intent(this,ChatActivity::class.java))
        }

        binding.appBarHome.edtSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                startActivity(Intent(this,MessagesActivity::class.java).putExtra("search",binding.appBarHome.edtSearch.text.toString())
                )
                true
            } else false
        })
    }
    fun selectMenuColor(v1: View, v2:View, v3:View, v4:View,v5:View){
        v1.setBackgroundColor(Color.parseColor("#d9d9d9"))
        v2.setBackgroundColor(Color.parseColor("#ffffff"))
        v3.setBackgroundColor(Color.parseColor("#ffffff"))
        v4.setBackgroundColor(Color.parseColor("#ffffff"))
        v5.setBackgroundColor(Color.parseColor("#ffffff"))

        binding.drawerLayout.closeDrawers()
    }

    fun loadCategory(){
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.loadCategory("Bearer " + appPreferences.user.auth_key,appPreferences.user.userID).observe(this@Home,
                androidx.lifecycle.Observer {
                    it.let {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this@Home)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                categoryList.clear()
                                filesList.clear()
                                it.data?.chatMessage?.messageList?.let { it1 ->
                                    categoryList.addAll(
                                        it1
                                    )
                                }
                                it.data?.chatMessage?.recentFiles?.let { it1->
                                    filesList.addAll(it1)
                                }

                                homeCategoryAdapter.notifyDataSetChanged()
                                homeFilesAdapter.notifyDataSetChanged()

                                if(filesList.size>0){
                                    binding.appBarHome.tvNoDocumentMessage.visibility = View.GONE
                                    binding.appBarHome.rvFiles.visibility = View.VISIBLE
                                }else{
                                    binding.appBarHome.tvNoDocumentMessage.visibility = View.VISIBLE
                                    binding.appBarHome.rvFiles.visibility = View.GONE
                                }

                                if(categoryList.size>0){
                                    binding.appBarHome.tvNoCategory.visibility = View.GONE
                                    binding.appBarHome.rvCategory.visibility = View.VISIBLE
                                }else{
                                    binding.appBarHome.tvNoCategory.visibility = View.VISIBLE
                                    binding.appBarHome.rvCategory.visibility = View.GONE
                                }

                                binding.appBarHome.tvSent.setText(Html.fromHtml("Sent <font color='#E91000'>("+it.data?.chatMessage?.fileCount?.get(0)?.sentCount.toString()+")</font>"))
                                binding.appBarHome.tvReceived.setText(Html.fromHtml("Received <font color='#E91000'>("+it.data?.chatMessage?.fileCount?.get(0)?.receiveCount.toString()+")</font"))

                                it.data?.chatMessage?.fileCount?.get(0)?.totalCount.apply {
                                    var totalCount =
                                        this?.toFloat()?.times(100)?.div(100)
                                            ?.let { it1 -> Math.round(it1) }
                                    totalCount?.let { it1 ->
                                        binding.appBarHome.progress.setProgress(
                                            it1.toInt(),true)
                                    }
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
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        selectMenuColor(binding.rlOne,binding.rlTwo,binding.rlThree,binding.rlFour,binding.rlFive)
        loadCategory()
        if(appPreferences.user!=null){
            binding.appBarHome.tvUsername.text = if(appPreferences.user.userName.isNotEmpty())appPreferences.user.userName else appPreferences.user.email
            binding.tvUserName.text = if(appPreferences.user.userName.isNotEmpty())appPreferences.user.userName else appPreferences.user.email
            if(!appPreferences.user.profile_image.isEmpty()) {
                Glide.with(this).load(RetrofitBuilder.BASE_URL_IMAGE+appPreferences.user.profile_image).into(binding.imageView)
                Glide.with(this).load(RetrofitBuilder.BASE_URL_IMAGE+appPreferences.user.profile_image).into(binding.appBarHome.imgUser)
            }

            binding.appBarHome.tvEmail.text = appPreferences.user.email
        }
    }

    fun logoutUser(){
        if (isNetworkAvailable) {
            val params = JSONObject()
            params.put("user_id", appPreferences.user.userID)

            viewModel.ChangePassword("Bearer "+appPreferences.user.auth_key,
                Gson().fromJson(
                    params.toString(),
                    JsonObject::class.java
                ),3).observe(this) {
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
        appPreferences.set("userObj","")
        finish()
        startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))

    }

    override fun onClick(model: categoryModel) {
        startActivity(Intent(this,MessagesActivity::class.java).putExtra("category",model.ID+"")
        )
    }

    override fun onClick(model: ReceivedMessage) {
        if(model.filesList.size>0) startActivity(Intent(this,DetailActivity::class.java).putExtra("images",model))
    }
}