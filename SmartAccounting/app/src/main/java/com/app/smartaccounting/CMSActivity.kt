package com.app.smartaccounting

import android.os.Build
import android.os.Bundle
import android.text.Html
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.smartaccounting.adapter.MenuAdapter
import com.app.smartaccounting.databinding.CmsActivityBinding
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class CMSActivity : BaseActivity() , CoroutineScope{
    private lateinit var binding: CmsActivityBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    var page:Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        page=intent.getIntExtra("page",0)
        appPreferences= AppPreferences(this)
        binding = DataBindingUtil.setContentView(this,R.layout.cms_activity)

        try {

            binding.imgBack.setOnClickListener { finish() }

            CoroutineScope(Dispatchers.Main).launch {

                viewModel.CMSMethod(page).observe(this@CMSActivity,
                    androidx.lifecycle.Observer {
                        it.let {
                            when (it.status) {
                                Status.LOADING -> {
                                    showLoading(this@CMSActivity)
                                }
                                Status.SUCCESS -> {
                                    hideLoading()

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        binding.tvContent
                                            .setText(
                                            Html.fromHtml(
                                                it.data?.content?.content,
                                                Html.FROM_HTML_MODE_COMPACT
                                            )
                                        )

                                        binding.tvName
                                            .setText(
                                                Html.fromHtml(
                                                    it.data?.content?.page,
                                                    Html.FROM_HTML_MODE_COMPACT
                                                )
                                            )

                                    } else {
                                        binding.tvContent
                                            .setText(Html.fromHtml( it.data?.content?.content))

                                        binding.tvName
                                            .setText(Html.fromHtml( it.data?.content?.page))
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

        }catch (e:Exception){
        }

    }
}