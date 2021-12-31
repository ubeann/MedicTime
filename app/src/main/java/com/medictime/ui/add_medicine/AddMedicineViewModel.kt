package com.medictime.ui.add_medicine

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences

class AddMedicineViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

}