package com.app.smartaccounting

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View.OnTouchListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.smartaccounting.databinding.LoginBinding
import com.app.smartaccounting.network.RetrofitBuilder.apiService
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.util.Util
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessaging.getInstance
import com.google.gson.Gson
import com.google.gson.JsonObject
import org.json.JSONObject


class LoginActivity : BaseActivity() {
    private lateinit var loginBinding: LoginBinding
    lateinit var viewModel: userViewModel
     var token=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // FirebaseApp.initializeApp(applicationContext)
        appPreferences= AppPreferences(this)
        loginBinding = DataBindingUtil.setContentView(this, R.layout.login)
        viewModel = ViewModelProvider(this, ViewModelFactory(apiService)).get(
            userViewModel::class.java
        )
        loginBinding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }



        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM-----------------", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            token = task.result
            Log.d("FCM-----------------", task.result.toString())

            // Log and toast
           // val msg = getString(R.string.msg_token_fmt, token)
           // Log.d(TAG, msg)
            //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })


        loginBinding.edtPassword.setOnTouchListener(OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= loginBinding.edtPassword.getRight() - loginBinding.edtPassword.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    if(loginBinding.edtPassword.getTransformationMethod().equals(
                            PasswordTransformationMethod.getInstance())){
                        //Show Password
                        loginBinding.edtPassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance());
                    }
                    else{
                        //Hide Password
                        loginBinding.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

                    }


                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })


        loginBinding.btnForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
            finish()
        }
        loginBinding.btnLogin.setOnClickListener {
            if (TextUtils.isEmpty(loginBinding.edtEmail.text.toString().trim())) {
                Util.showDialog(this, "Please enter a email address")
            } else if (!Util.isValidEmail(loginBinding.edtEmail.text.toString().trim())) {
                Util.showDialog(this, " Please enter valid email address")
            } else if (TextUtils.isEmpty(loginBinding.edtPassword.text.toString().trim())) {
                Util.showDialog(this, "Please enter password")
            } else {
                if (isNetworkAvailable) {
                    val params = JSONObject()
                    params.put("email", loginBinding.edtEmail.text.toString())
                    params.put("password", loginBinding.edtPassword.text.toString())
                    params.put("device_token", token)
                    params.put("device_type", "mobile")
                    params.put("device_model", "android")
                    params.put("app_version", "1.0")
                    params.put("os_version", "12")
                    viewModel.LoginorRegistration(
                        Gson().fromJson(
                            params.toString(),
                            JsonObject::class.java
                        ), 1
                    ).observe(this) {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                if (it.data?.status == 200) {
                                    if(it.data.success) {
                                        Log.d("CAlled", it.data?.userData?.auth_key.toString())
                                        appPreferences.set(
                                            "TOKEN",
                                            it.data?.userData?.auth_key.toString()
                                        )
                                        val gson = Gson()
                                        val json = gson.toJson(it.data?.userData)
                                        appPreferences.set("userObj", json)
                                        finish()
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                Home::class.java
                                            )
                                        )
                                    }else{
                                        showToast(it.data.message)
                                    }
                                } else {
                                    showToast(it.data?.message)
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
                } else {
                    showToast(getString(R.string.no_net))
                }
            }
        }
    }
}