package com.medictime.ui.edit_medicine

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.medictime.entity.Medicine
import com.medictime.entity.User
import com.medictime.helper.Event
import com.medictime.helper.NotificationMedicine
import com.medictime.preferences.UserPreferences
import com.medictime.repository.MedicineRepository
import com.medictime.repository.UserRepository
import java.time.OffsetDateTime
import java.time.OffsetTime

class EditMedicineViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mMedicineRepository: MedicineRepository = MedicineRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    private val notification: NotificationMedicine = NotificationMedicine()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserIdByEmail(email: String): Int = mUserRepository.getUserByEmail(email).id

    fun editMedicine(context: Context, user_id: Int, medicine: Medicine, medicine_name: String, medicine_type: String, medicine_description: String, medicine_date: OffsetDateTime, medicine_time: OffsetTime, medicine_amount: Int) {
        with(medicine) {
            userId = user_id
            name = medicine_name
            type = medicine_type
            description = medicine_description
            dateTime = medicine_time.atDate(medicine_date.toLocalDate())
            amount = medicine_amount
        }
        mMedicineRepository.update(medicine)
        notification.delete(context, medicine.id)
        notification.set(
            context,
            medicine.id,
            "${medicine.amount}x ${medicine.name} (${medicine.type})",
            medicine.description,
            medicine.dateTime
        )
        _notificationText.value = Event("Success update $medicine_name on database")
    }

    fun deleteMedicine(context: Context, medicine: Medicine) {
        notification.delete(context, medicine.id)
        mMedicineRepository.delete(medicine)
        _notificationText.value = Event("Success delete ${medicine.name} on database")
    }
}