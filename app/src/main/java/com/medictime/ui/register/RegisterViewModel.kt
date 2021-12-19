package com.medictime.ui.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medictime.auth.AESEncryption
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences
import com.medictime.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class RegisterViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun register(userName: String, userEmail: String, userPhone: String, userPassword: String, userCreatedAt: OffsetDateTime) {
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, userPhone, userCreatedAt)
        }
        val user = User(
            name = userName,
            email = userEmail,
            phone = userPhone,
            password = AESEncryption.encrypt(userPassword),
            createdAt = userCreatedAt
        )
        mUserRepository.insert(user)
    }

    fun isEmailRegistered(email: String): Boolean = mUserRepository.isEmailRegistered(email)

    fun isUsernameRegistered(userName: String): Boolean = mUserRepository.isUsernameRegistered(userName)
}