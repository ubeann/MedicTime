package com.medictime.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()
}