package com.medictime.ui.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.User
import com.medictime.entity.relation.UserMedicine
import com.medictime.helper.Event
import com.medictime.preferences.UserPreferences
import com.medictime.repository.MedicineRepository
import com.medictime.repository.UserRepository
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mMedicineRepository: MedicineRepository = MedicineRepository(application)
    private val _dayEpoch = MutableLiveData<Event<Long>>()
    val dayEpoch: LiveData<Event<Long>> = _dayEpoch

    init {
        val calendar = Calendar.getInstance()
        _dayEpoch.value = Event(OffsetDateTime.of(calendar.get(Calendar.YEAR) - 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH) - 0, 0, 0,0,0, ZoneOffset.UTC).toInstant().toEpochMilli())
    }

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserIdByEmail(email: String): Int = mUserRepository.getUserByEmail(email).id

    fun getUserMedicine(userId: Int, dateTime: OffsetDateTime) : LiveData<List<UserMedicine>> = mMedicineRepository.getUserMedicine(userId, dateTime)

    fun setDayEpoch(dayEpoch: Long) {
        _dayEpoch.value = Event(dayEpoch)
    }
}