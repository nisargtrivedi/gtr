package com.app.smartaccounting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import com.app.smartaccounting.util.AppPreferences

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appPreferences= AppPreferences(this)
        Handler(Looper.getMainLooper()).postDelayed({
          //  finish()
                if(appPreferences.user!=null && !TextUtils.isEmpty(appPreferences.user.userID)) {
                    startActivity(Intent(this,Home::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                }else{
                    startActivity(Intent(this,LoginActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
                }
        },2000)
    }
}