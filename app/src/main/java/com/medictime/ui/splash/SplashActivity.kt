package com.medictime.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.medictime.R
import com.medictime.ui.register.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val loadingTime:Long = 3000 //Delay of 3 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val splashThread: Thread = object : Thread() {
            override fun run() {
                try {
                    super.run()
                    sleep(loadingTime)
                } catch (e: Exception) {
                } finally {
                    searchIntent()
                }
            }
        }
        splashThread.start()
    }

    private fun searchIntent() {
        val intent = Intent(this@SplashActivity, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }
}