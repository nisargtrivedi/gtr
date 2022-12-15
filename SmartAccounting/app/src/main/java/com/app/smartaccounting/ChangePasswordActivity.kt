package com.app.smartaccounting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.app.smartaccounting.databinding.ChangePasswordBinding
import com.app.smartaccounting.databinding.ForgotPasswordBinding
import com.app.smartaccounting.databinding.SignupBinding
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
import kotlin.coroutines.CoroutineContext

class ChangePasswordActivity : BaseActivity() , CoroutineScope{
    private lateinit var signupBinding: ChangePasswordBinding
    private lateinit var job: Job
    lateinit var viewModel: userViewModel

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        viewModel = ViewModelProvider(this, ViewModelFactory(RetrofitBuilder.apiService)).get(
            userViewModel::class.java
        )
        appPreferences= AppPreferences(this)
        signupBinding = DataBindingUtil.setContentView(this,R.layout.change_password)

        signupBinding.btnSave.setOnClickListener {
            if (TextUtils.isEmpty(signupBinding.edtCurrentPassword.text.toString().trim())) {
                Util.showDialog(this, "Please enter current password")
            } else if (TextUtils.isEmpty(signupBinding.edtNewPassword.text.toString().trim())) {
                Util.showDialog(this, " Please enter new password")
            } else if (TextUtils.isEmpty(signupBinding.edtConfirmPassword.text.toString().trim())) {
                Util.showDialog(this, " Please enter confirm password")
            } else if (signupBinding.edtConfirmPassword.text.toString()!=signupBinding.edtNewPassword.text.toString()) {
                Util.showDialog(this, "Confirm password and new password not match")
            } else {
                if (isNetworkAvailable) {
                    val params = JSONObject()
                   params.put("user_id", appPreferences.user.userID)
                    params.put("current_password", signupBinding.edtCurrentPassword.text.toString())
                    params.put("new_password", signupBinding.edtNewPassword.text.toString())

                    viewModel.ChangePassword("Bearer "+appPreferences.user.auth_key,
                        Gson().fromJson(
                            params.toString(),
                            JsonObject::class.java
                        ),1).observe(this) {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                                val builder1 = AlertDialog.Builder(this)
                                builder1.setMessage(it.data?.message ?: "")
                                builder1.setTitle("Alert")
                                builder1.setCancelable(true)

                                builder1.setPositiveButton(
                                    "Ok"
                                ) { dialog, id ->
                                    dialog.cancel()
                                    dialog.dismiss()
                                    finish()
                                }
                                builder1.setNegativeButton(
                                    "Cancel"
                                ) { dialog, id ->
                                    dialog.cancel()
                                    dialog.dismiss()
                                }
                                val alert11 = builder1.create()
                                alert11.show()
                              //  finish()
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

        signupBinding.edtCurrentPassword.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >=signupBinding.edtCurrentPassword.getRight() - signupBinding.edtCurrentPassword.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    if (signupBinding.edtCurrentPassword.getTransformationMethod().equals(
                            PasswordTransformationMethod.getInstance()
                        )
                    ) {
                        //Show Password
                        signupBinding.edtCurrentPassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance()
                        );
                    } else {
                        //Hide Password
                        signupBinding.edtCurrentPassword.setTransformationMethod(
                            PasswordTransformationMethod.getInstance()
                        );

                    }


                    return@OnTouchListener true
                }
            }
            return@OnTouchListener false
        })

        signupBinding.edtNewPassword.setOnTouchListener(View.OnTouchListener { v, event ->
            val DRAWABLE_LEFT = 0
            val DRAWABLE_TOP = 1
            val DRAWABLE_RIGHT = 2
            val DRAWABLE_BOTTOM = 3
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >=signupBinding.edtNewPassword.getRight() - signupBinding.edtNewPassword.getCompoundDrawables()
                        .get(DRAWABLE_RIGHT).getBounds().width()
                ) {
                    // your action here
                    if (signupBinding.edtNewPassword.getTransformationMethod().equals(
                            PasswordTransformationMethod.getInstance()
                        )
                    ) {
                        //Show Password
                        signupBinding.edtNewPassword.setTransformationMethod(
                            HideReturnsTransformationMethod.getInstance()
                        );
                    } else {
                        //Hide Password
                        signupBinding.edtNewPassword.setTransformationMethod(
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
                if (event.rawX >=signupBinding.edtConfirmPassword.getRight() - signupBinding.edtConfirmPassword.getCompoundDrawables()
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


    }

}