package com.medictime.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences

class MainViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()
}