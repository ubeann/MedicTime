package com.medictime.ui.edit_medicine

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.User
import com.medictime.helper.Event
import com.medictime.helper.NotificationMedicine
import com.medictime.preferences.UserPreferences
import com.medictime.repository.MedicineRepository
import com.medictime.repository.UserRepository

class EditMedicineViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mMedicineRepository: MedicineRepository = MedicineRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    private val notification: NotificationMedicine = NotificationMedicine()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserIdByEmail(email: String): Int = mUserRepository.getUserByEmail(email).id
}