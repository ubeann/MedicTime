package com.medictime.ui.edit_medicine

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.medictime.App
import com.medictime.databinding.ActivityEditMedicineBinding
import com.medictime.entity.Medicine
import com.medictime.preferences.UserPreferences
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
import java.util.*

class EditMedicineActivity : AppCompatActivity() {
    private var _binding: ActivityEditMedicineBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private var dayEpoch: Long = OffsetDateTime.now().toInstant().toEpochMilli()
    private var timeEpoch: OffsetTime = OffsetTime.now()
    private var userId: Int = 0
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: EditMedicineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityEditMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val medicine = intent.getParcelableExtra<Medicine>(EXTRA_MEDICINE)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, EditMedicineViewModelFactory(application, preferences))[EditMedicineViewModel::class.java]

        viewModel.getUserSetting().observe(this, { dataUser ->
            userId = if (dataUser.email.isNotEmpty()) viewModel.getUserIdByEmail(dataUser.email) else 0
        })
    }

    companion object {
        const val EXTRA_MEDICINE = "medicine"
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_TAG = "TimePicker"
    }
}