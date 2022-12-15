package com.app.smartaccounting

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.smartaccounting.adapter.MenuAdapter
import com.app.smartaccounting.databinding.SettingsBinding
import com.app.smartaccounting.listeners.onImageDelete
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.util.Util
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONObject
import java.io.*
import java.util.*
import kotlin.coroutines.CoroutineContext

class SettingsActivity : BaseActivity() , CoroutineScope,onImageDelete{
    private lateinit var binding: SettingsBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel
    var menuList = mutableListOf<String>()
    lateinit var menuAdapter: MenuAdapter
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        appPreferences= AppPreferences(this)
        binding = DataBindingUtil.setContentView(this,R.layout.settings)

        try {
            menuList.add("Change Password")
            menuList.add("About Us")
            menuList.add("Privacy Policy")
            menuList.add("Terms & Condition")
            menuList.add("Delete Account")
            menuList.add("Logout")
            menuAdapter = MenuAdapter(this,menuList)
            binding.lstMenu.adapter = menuAdapter

            menuAdapter.selectMenuItem(this)
            binding.imgBack.setOnClickListener { finish() }

        }catch (e:Exception){
        }

    }

    override fun onClick(model: String, pos: Int) {
        if(model=="About Us"){
            startActivity(Intent(this,CMSActivity::class.java).putExtra("page",1))
        }else if(model=="Terms & Condition"){
            startActivity(Intent(this,CMSActivity::class.java).putExtra("page",2))
        }else if(model=="Privacy Policy"){
            startActivity(Intent(this,CMSActivity::class.java).putExtra("page",3))
        }else if(model=="Delete Account"){

            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage("Are you sure want to delete this account ?")
            builder1.setTitle("Alert")
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "Ok"
            ) { dialog, id ->
                dialog.cancel()
                dialog.dismiss()
                deleteUser()
            }
            builder1.setNegativeButton(
                    "Cancel"
            ) { dialog, id ->
                dialog.cancel()
                dialog.dismiss()
            }
            val alert11 = builder1.create()
            alert11.show()

        }
        else if(model=="Change Password"){
            startActivity(Intent(this,ChangePasswordActivity::class.java))
        }
        else if(model=="Logout"){

            val builder1 = AlertDialog.Builder(this)
            builder1.setMessage("Are you sure want to logout from this account ?")
            builder1.setTitle("Alert")
            builder1.setCancelable(true)

            builder1.setPositiveButton(
                "Ok"
            ) { dialog, id ->
                dialog.cancel()
                dialog.dismiss()
                logoutUser()
            }
            builder1.setNegativeButton(
                "Cancel"
            ) { dialog, id ->
                dialog.cancel()
                dialog.dismiss()
            }
            val alert11 = builder1.create()
            alert11.show()

        }
    }

    fun deleteUser(){
        if (isNetworkAvailable) {
            val params = JSONObject()
            params.put("user_id", appPreferences.user.userID)

            viewModel.ChangePassword("Bearer "+appPreferences.user.auth_key,
                Gson().fromJson(
                    params.toString(),
                    JsonObject::class.java
                ),2).observe(this) {
                when (it.status) {
                    Status.LOADING -> {
                        showLoading(this)
                    }
                    Status.SUCCESS -> {
                        hideLoading()
                        finishAffinity()
                        startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))

                    }
                    Status.ERROR -> {
                        hideLoading()
                        Util.showDialog(this,it.message)
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
                        appPreferences.set("userObj","")
                        finishAffinity()
                        startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                    Status.ERROR -> {
                        hideLoading()
                        Util.showDialog(this,it.message)
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