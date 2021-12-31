package com.medictime.ui.splash

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.medictime.App
import com.medictime.R
import com.medictime.preferences.UserPreferences
import com.medictime.ui.main.MainActivity
import com.medictime.ui.register.RegisterActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private val loadingTime:Long = 3000 //Delay of 3 seconds
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private var isUserAlreadyLogin: Boolean = false
    private var isUserRegistered: Boolean = false
    private lateinit var userPreferences: UserPreferences
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Set preferences and viewModel
        userPreferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, SplashViewModelFactory(application, userPreferences))[SplashViewModel::class.java]

        // Observe if User already login
        viewModel.getUserLogin().observe(this, {
            isUserAlreadyLogin = it

            // Observe if User is registered
            if (isUserAlreadyLogin) {
                viewModel.getUserSetting().observe(this, { user ->
                    isUserRegistered = viewModel.isRegistered(user.email) and user.email.isNotEmpty()
                })
            }
        })

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
        val intent = Intent(this@SplashActivity,
            if (isUserAlreadyLogin and isUserRegistered)
                MainActivity::class.java
            else
                RegisterActivity::class.java
        )
        startActivity(intent)
        finish()
    }
}