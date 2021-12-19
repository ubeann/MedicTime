package com.medictime.ui.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.medictime.App
import com.medictime.databinding.ActivityRegisterBinding
import com.medictime.preferences.UserPreferences
import com.medictime.ui.login.LoginActivity
import com.medictime.ui.main.MainActivity
import java.time.OffsetDateTime

class RegisterActivity : AppCompatActivity() {
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(application, preferences))[RegisterViewModel::class.java]

        binding.signupBtn.setOnClickListener {
            closeKeyboard()

            val isUsernameEmpty = TextUtils.isEmpty(binding.usernameInput.text.toString().trim())
            val isEmailEmpty = TextUtils.isEmpty(binding.emailInput.text.toString().trim())
            val isPhoneEmpty = TextUtils.isEmpty(binding.phoneInput.text.toString().trim())
            val isPasswordEmpty = TextUtils.isEmpty(binding.passwordInput.text.toString())
            val isEmailRegistered = if (isEmailEmpty) true else viewModel.isEmailRegistered(binding.emailInput.text.toString().trim())
            val isUsernameRegistered = if (isUsernameEmpty) true else viewModel.isUsernameRegistered(binding.usernameInput.text.toString().trim())

            when {
                isUsernameEmpty or isEmailEmpty or isPhoneEmpty or isPasswordEmpty -> showSnackBar("Please fill all required input field")
                isEmailRegistered -> showSnackBar("Email already registered, please use other email")
                isUsernameRegistered -> showSnackBar("Username already exist, please use other username")
                else -> {
                    viewModel.register(
                        userName = binding.usernameInput.text.toString().lowercase(),
                        userEmail = binding.emailInput.text.toString().lowercase(),
                        userPhone = binding.phoneInput.text.toString().trim(),
                        userPassword = binding.passwordInput.text.toString(),
                        userCreatedAt = OffsetDateTime.now()
                    )

                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.loginBtn.setOnClickListener {
            closeKeyboard()

            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT)
            .show()
    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}