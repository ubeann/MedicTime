package com.medictime.ui.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences
import com.medictime.repository.UserRepository

class SplashViewModel  (application: Application, private val user: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun getUserLogin(): LiveData<Boolean> = user.getUserIsLogin().asLiveData()

    fun getUserSetting(): LiveData<User> = user.getUserSetting().asLiveData()

    fun isRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)
}