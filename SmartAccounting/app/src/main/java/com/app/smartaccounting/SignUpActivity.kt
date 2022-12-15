package com.app.smartaccounting

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ContentInfoCompat.Flags
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.smartaccounting.databinding.SignupBinding
import com.app.smartaccounting.network.RetrofitBuilder
import com.app.smartaccounting.util.AppPreferences
import com.app.smartaccounting.util.Status
import com.app.smartaccounting.util.Util
import com.app.smartaccounting.viewmodels.ViewModelFactory
import com.app.smartaccounting.viewmodels.userViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.json.JSONObject
import kotlin.coroutines.CoroutineContext

class SignUpActivity : BaseActivity() , CoroutineScope{
    private lateinit var signupBinding: SignupBinding
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    lateinit var viewModel: userViewModel
    var token=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        appPreferences= AppPreferences(this)
        signupBinding = DataBindingUtil.setContentView(this,R.layout.signup)
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )

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


        signupBinding.edtPassword.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= signupBinding.edtPassword.getRight() - signupBinding.edtPassword.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    if (signupBinding.edtPassword.getTransformationMethod().equals(
                            PasswordTransformationMethod.getInstance()
                        )
                    ) {
                        //Show Password
                        signupBinding.edtPassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance()
                        );
                    } else {
                        //Hide Password
                        signupBinding.edtPassword.setTransformationMethod(
                            PasswordTransformationMethod.getInstance()
                        );

                    }


                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })

        signupBinding.edtConfirmPassword.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= signupBinding.edtConfirmPassword.getRight() - signupBinding.edtConfirmPassword.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    if (signupBinding.edtConfirmPassword.getTransformationMethod().equals(
                            PasswordTransformationMethod.getInstance()
                        )
                    ) {
                        //Show Password
                        signupBinding.edtConfirmPassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance()
                        );
                    } else {
                        //Hide Password
                        signupBinding.edtConfirmPassword.setTransformationMethod(
                            PasswordTransformationMethod.getInstance()
                        );

                    }


                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })


        signupBinding.btnRegister.setOnClickListener {

            if(TextUtils.isEmpty(signupBinding.edtUsername.text.toString().trim())){
                Util.showDialog(this, "Please enter username")
            }else if(TextUtils.isEmpty(signupBinding.edtEmail.text.toString().trim())){
                Util.showDialog(this, "Please enter a email address")
            } else if(!Util.isValidEmail(signupBinding.edtEmail.text.toString().trim())){
                Util.showDialog(this, " Please enter valid email address")
            }else if(TextUtils.isEmpty(signupBinding.edtMobile.text.toString().trim())){
                Util.showDialog(this, "Please enter a mobile number")
            } else if(signupBinding.edtMobile.text.toString().length<10){
                Util.showDialog(this, "Please enter valid mobile number")
            } else if(TextUtils.isEmpty(signupBinding.edtPassword.text.toString().trim())){
                Util.showDialog(this, "Please enter password")
            }else if(signupBinding.edtPassword.text.toString()!=signupBinding.edtConfirmPassword.text.toString()){
                Util.showDialog(this, "Confirm password and password not match.")
            }else{
                if(isNetworkAvailable){
                    val params = JSONObject()
                    params.put("username",signupBinding.edtUsername.text.toString())
                    params.put("phone",signupBinding.edtMobile.text.toString())
                    params.put("email",signupBinding.edtEmail.text.toString())
                    params.put("password",signupBinding.edtPassword.text.toString())
                    params.put("device_token",token)
                    params.put("device_type","mobile")
                    params.put("device_model","android")
                    params.put("app_version","1.0")
                    params.put("os_version","12")
                    viewModel.LoginorRegistration(Gson().fromJson(params.toString(), JsonObject::class.java),2).observe(this,{
                        when(it.status){
                            Status.LOADING -> {
                                showLoading(this)
                            }
                            Status.SUCCESS ->{
                                hideLoading()
                                showToast(it.data?.message)
                                if(it?.data?.success == true) startActivity(Intent(this@SignUpActivity,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  or Intent.FLAG_ACTIVITY_NEW_TASK))
                            }
                            Status.ERROR ->{
                                hideLoading()
                                showToast(it.message)
                            }
                            else -> {
                                hideLoading()
                            }
                        }

                    })
                }else{
                    showToast(getString(R.string.no_net))
                }
            }
        }


        signupBinding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }
}