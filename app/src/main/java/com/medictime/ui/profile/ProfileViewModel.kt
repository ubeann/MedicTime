package com.medictime.ui.profile

import android.app.Application
import androidx.lifecycle.*
import com.medictime.auth.AESEncryption
import com.medictime.entity.User
import com.medictime.helper.Event
import com.medictime.preferences.UserPreferences
import com.medictime.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserByEmail(email: String): User = mUserRepository.getUserByEmail(email)

    fun isEmailValid(email: String): Boolean = !mUserRepository.isEmailRegistered(email)

    fun isUsernameValid(userName: String): Boolean = !mUserRepository.isUsernameRegistered(userName)

    fun updateProfile(user: User, userName: String, userEmail: String, userPhone: String) {
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, userPhone, user.createdAt)
        }
        with(user) {
            name = userName
            email = userEmail
            phone = userPhone
        }
        mUserRepository.update(user)
        _notificationText.value = Event("Success update profile $userName on database")
    }

    fun isPasswordMatch(userName: String, oldPassword: String): Boolean {
        val user = mUserRepository.getUserByName(userName)
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun updatePassword(user: User, newPassword: String) {
        user.password = AESEncryption.encrypt(newPassword)
        mUserRepository.update(user)
        _notificationText.value = Event("Success change password of ${user.name} on database")
    }

    fun forgetUserLogin(isForget: Boolean) {
        viewModelScope.launch {
            preferences.forgetUserLogin(!isForget)
        }
    }
}