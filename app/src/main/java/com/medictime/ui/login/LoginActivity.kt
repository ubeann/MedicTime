package com.medictime.ui.login

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
import com.medictime.databinding.ActivityLoginBinding
import com.medictime.preferences.UserPreferences
import com.medictime.ui.main.MainActivity
import com.medictime.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, LoginViewModelFactory(application, preferences))[LoginViewModel::class.java]

        binding.loginBtn.setOnClickListener {
            closeKeyboard()

            val isUsernameEmpty = TextUtils.isEmpty(binding.usernameInput.text.toString().trim())
            val isPasswordEmpty = TextUtils.isEmpty(binding.passwordInput.text.toString())
            val isNotRegistered = if (isUsernameEmpty) false else !viewModel.isRegistered(binding.usernameInput.text.toString().lowercase().trim())
            val isPasswordNotMatch = if (isNotRegistered) true else !viewModel.isUserPasswordMatch(binding.usernameInput.text.toString().lowercase().trim(), binding.passwordInput.text.toString())

            when {
                isUsernameEmpty or isPasswordEmpty -> showSnackBar("Please fill all required input field")
                isNotRegistered -> showSnackBar("Username is not registered, please sign up first")
                isPasswordNotMatch -> showSnackBar("Password is wrong, please try again")
                else -> {
                    viewModel.login(userName = binding.usernameInput.text.toString().lowercase().trim())
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }

        binding.signupBtn.setOnClickListener {
            closeKeyboard()

            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
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