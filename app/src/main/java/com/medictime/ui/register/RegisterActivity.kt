package com.medictime.ui.register

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.medictime.App
import com.medictime.databinding.ActivityRegisterBinding
import com.medictime.preferences.UserPreferences

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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}