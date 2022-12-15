package com.app.smartaccounting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
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

class ForgotPasswordActivity : BaseActivity() , CoroutineScope{
    private lateinit var signupBinding: ForgotPasswordBinding
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
        signupBinding = DataBindingUtil.setContentView(this,R.layout.forgot_password)

        signupBinding.btnSave.setOnClickListener {
            if (TextUtils.isEmpty(signupBinding.edtEmail.text.toString().trim())) {
                Util.showDialog(this, "Please enter a email address")
            } else if (!Util.isValidEmail(signupBinding.edtEmail.text.toString().trim())) {
                Util.showDialog(this, " Please enter valid email address")
            } else {
                if (isNetworkAvailable) {
                    val params = JSONObject()
                    params.put("email", signupBinding.edtEmail.text.toString())
                    viewModel.LoginorRegistration(
                        Gson().fromJson(
                            params.toString(),
                            JsonObject::class.java
                        ), 3
                    ).observe(this) {
                        when (it.status) {
                            Status.LOADING -> {
                                showLoading(this)
                            }
                            Status.SUCCESS -> {
                                hideLoading()
                               // Util.showDialog(this,it.message)

                                val builder1 = AlertDialog.Builder(this)
                                builder1.setMessage(it.data?.message ?: "")
                                builder1.setTitle("Alert")
                                builder1.setCancelable(true)

                                builder1.setPositiveButton(
                                    "Ok"
                                ) { dialog, id ->
                                    dialog.cancel()
                                    dialog.dismiss()
                                    startActivity(
                                        Intent(
                                            this@ForgotPasswordActivity,
                                            LoginActivity::class.java
                                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    )
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
                            Status.ERROR -> {
                                hideLoading()
                                Util.showDialog(this,it.message)
                            }
                            else -> {
                                Util.showDialog(this,it.message)
                            }
                        }

                    }
                } else {
                    showToast(getString(R.string.no_net))
                }
            }
        }

        signupBinding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

}