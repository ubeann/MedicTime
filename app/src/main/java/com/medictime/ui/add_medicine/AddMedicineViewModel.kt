package com.medictime.ui.add_medicine

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.Medicine
import com.medictime.entity.User
import com.medictime.helper.Event
import com.medictime.preferences.UserPreferences
import com.medictime.repository.MedicineRepository
import com.medictime.repository.UserRepository
import java.time.OffsetDateTime
import java.time.OffsetTime

class AddMedicineViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mMedicineRepository: MedicineRepository = MedicineRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserIdByEmail(email: String): Int = mUserRepository.getUserByEmail(email).id

    fun addMedicine(user_id: Int, medicine_name: String, medicine_type: String, medicine_description: String, medicine_date: OffsetDateTime, medicine_time: OffsetTime, medicine_amount: Int) {
        val data = Medicine(
            userId = user_id,
            name = medicine_name,
            type = medicine_type,
            description = medicine_description,
            dateTime = medicine_time.atDate(medicine_date.toLocalDate()),
            amount = medicine_amount
        )
        mMedicineRepository.insert(data)
        _notificationText.value = Event("Success add $medicine_name to database")
    }
}