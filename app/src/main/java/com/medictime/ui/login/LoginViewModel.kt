package com.medictime.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medictime.auth.AESEncryption
import com.medictime.preferences.UserPreferences
import com.medictime.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun isRegistered(userName: String): Boolean = mUserRepository.isUsernameRegistered(userName)

    fun isUserPasswordMatch(userName: String, oldPassword: String): Boolean {
        // Get User Data
        val user = mUserRepository.getUserByName(userName)

        // Return
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun login(userName: String) {
        // Get User Data
        val user = mUserRepository.getUserByName(userName)

        // Save User Data to Setting
        viewModelScope.launch {
            preferences.saveUserSetting(
                userName = user.name,
                userEmail = user.email,
                userPhone = user.phone,
                userCreatedAt = user.createdAt
            )
        }
    }
}