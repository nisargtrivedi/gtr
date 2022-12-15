package com.app.smartaccounting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.smartaccounting.databinding.ForgotPasswordBinding
import com.app.smartaccounting.databinding.HomeBinding
import com.app.smartaccounting.databinding.SignupBinding
import com.app.smartaccounting.util.AppPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class HomeActivity : BaseActivity() , CoroutineScope{
    private lateinit var signupBinding: HomeBinding
    private lateinit var job: Job

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + job
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
        appPreferences = AppPreferences(this)
        signupBinding = DataBindingUtil.setContentView(this,R.layout.home)
        if(appPreferences.user!=null){
            signupBinding.tvUsername.text = if(appPreferences.user.userName.isNotEmpty())appPreferences.user.userName else appPreferences.user.email
        }
        signupBinding.btnHome.setOnClickListener {
            startActivity(Intent(this,UpdateProfileActivity::class.java))
        }
     /*   signupBinding.menuOne.setOnClickListener {
            startActivity(Intent(this,ChatActivity::class.java))
        }
        signupBinding.menuTwo.setOnClickListener {
            startActivity(Intent(this,ReceivedActivity::class.java)
                .putExtra("name","two")
            )
        }
        signupBinding.menuThree.setOnClickListener {
            startActivity(Intent(this,ReceivedActivity::class.java)
                .putExtra("name","three")
            )
        }*/
        signupBinding.imgSettings.setOnClickListener {
            startActivity(Intent(this,SettingsActivity::class.java)
            )
        }
    }
}