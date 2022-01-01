package com.medictime.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.medictime.App
import com.medictime.databinding.ActivityProfileBinding
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences
import com.medictime.ui.about_us.AboutUsActivity
import com.medictime.ui.login.LoginActivity

class ProfileActivity : AppCompatActivity() {
    private var _binding: ActivityProfileBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var user: User
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: ProfileViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, ProfileViewModelFactory(application, preferences))[ProfileViewModel::class.java]

        viewModel.getUserSetting().observe(this, { dataUser ->
            if (dataUser.email.isNotEmpty()) {
                user =  viewModel.getUserByEmail(dataUser.email)
            }

            with(binding) {
                inputUsername.editText?.setText(dataUser.name)
                inputEmail.editText?.setText(dataUser.email)
                inputPhone.editText?.setText(dataUser.phone)
            }
        })

        binding.aboutUs.setOnClickListener {
            val intent = Intent(this@ProfileActivity, AboutUsActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener {
            closeKeyboard()
            viewModel.forgetUserLogin(true)
            val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        binding.btnSave.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isUsernameFilled = isInputFilled(inputUsername, "Please fill your username")
                val isUsernameValid = if (isUsernameFilled and (inputUsername.editText?.text.toString() != user.name)) isUsernameValid(inputUsername) else false
                val isEmailFilled = isInputFilled(inputEmail, "Please fill your email address")
                val isEmailValid = if (isEmailFilled and (inputEmail.editText?.text.toString() != user.email)) isEmailValid(inputEmail) else false
                val isPhoneFilled = isInputFilled(inputPhone, "Please fill your phone number")

                if (isUsernameFilled and isUsernameValid and isEmailFilled and isEmailValid and isPhoneFilled) {
                    viewModel.updateProfile(
                        user,
                        inputUsername.editText?.text.toString().trim().lowercase(),
                        inputEmail.editText?.text.toString().trim().lowercase(),
                        inputPhone.editText?.text.toString().trim()
                    )
                }
            }
        }

        binding.btnPassword.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isOldPasswordFilled = isInputFilled(inputOldPassword, "Please fill your old password")
                val isOldPasswordValid = if (isOldPasswordFilled) isPasswordValid(inputOldPassword) else false
                val isNewPasswordFilled = isInputFilled(inputNewPassword, "Please fill your new password")

                if (isOldPasswordFilled and isOldPasswordValid and isNewPasswordFilled) {
                    viewModel.updatePassword(
                        user,
                        inputNewPassword.editText?.text.toString().trim()
                    )
                    inputOldPassword.editText?.setText("")
                    inputNewPassword.editText?.setText("")
                }
            }
        }

        viewModel.notificationText.observe(this, {
            it.getContentIfNotHandled()?.let { text ->
                showSnackBar(text)
            }
        })

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun isInputFilled(view: TextInputLayout, error: String) : Boolean {
        view.editText?.clearFocus()
        return if (TextUtils.isEmpty(view.editText?.text.toString().trim())) {
            view.isErrorEnabled = true
            view.error = error
            false
        } else {
            view.isErrorEnabled = false
            true
        }
    }

    private fun isEmailValid(view: TextInputLayout, error: String = "Email already registered, please use other email") : Boolean {
        view.editText?.clearFocus()
        return if (viewModel.isEmailValid(view.editText?.text.toString().trim())){
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun isUsernameValid(view: TextInputLayout, error: String = "Username already registered, please use other username") : Boolean {
        view.editText?.clearFocus()
        return if (viewModel.isUsernameValid(view.editText?.text.toString().trim())){
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun isPasswordValid(view: TextInputLayout, error: String = "Password is not match in our records, please try again") : Boolean {
        view.editText?.clearFocus()
        return if (viewModel.isPasswordMatch(user.name, view.editText?.text.toString().trim())){
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}