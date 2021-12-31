package com.medictime.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.medictime.App
import com.medictime.R
import com.medictime.databinding.ActivityMainBinding
import com.medictime.entity.User
import com.medictime.preferences.UserPreferences
import com.medictime.ui.add_medicine.AddMedicineActivity
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    private val activityFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private var dayEpoch: Long = OffsetDateTime.now().toInstant().toEpochMilli()
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: MainViewModel
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application, preferences))[MainViewModel::class.java]

        viewModel.getUserSetting().observe(this, { dataUser ->
            user = dataUser
            with(binding) {
                headerName.text = dataUser.name
                headerDate.text = dateFormat.format(dayEpoch)
                todayActivity.text = resources.getString(R.string.your_today_s_activities, activityFormat.format(dayEpoch))
            }
        })

        binding.datePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val start = LocalDateTime
                .of(
                    calendar.get(Calendar.YEAR) - 0,
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH) - 0,
                    0,
                    0,
                    0,
                )
                .atZone(ZoneId.ofOffset("UTC", ZoneOffset.UTC))
                .toInstant()
                .toEpochMilli()
            val constraintsBuilder = CalendarConstraints.Builder()
                .setStart(start)
                .setOpenAt(dayEpoch)
                .setValidator(DateValidatorPointForward.from(start))
            val picker = MaterialDatePicker.Builder.datePicker()
                .setSelection(dayEpoch)
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
            picker.show(this.supportFragmentManager, DATE_PICKER_TAG)
            picker.addOnPositiveButtonClickListener {
                dayEpoch = it
                with(binding) {
                    todayActivity.text = resources.getString(R.string.your_today_s_activities, activityFormat.format(dayEpoch))
                }
            }
        }

        binding.addMedicine.setOnClickListener {
            val intent = Intent(this@MainActivity, AddMedicineActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }
}