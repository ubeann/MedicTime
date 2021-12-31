package com.medictime.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.medictime.App
import com.medictime.R
import com.medictime.adapter.MedicineAdapter
import com.medictime.databinding.ActivityMainBinding
import com.medictime.entity.User
import com.medictime.entity.relation.UserMedicine
import com.medictime.preferences.UserPreferences
import com.medictime.ui.add_medicine.AddMedicineActivity
import com.medictime.ui.edit_medicine.EditMedicineActivity
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    private val activityFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()
    private var dayEpoch: Long = OffsetDateTime.of(calendar.get(Calendar.YEAR) - 0, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH) - 0, 0, 0,0,0, ZoneOffset.UTC).toInstant().toEpochMilli()
    private var userId: Int = 0
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: MainViewModel
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding.listMedicine) {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, MainViewModelFactory(application, preferences))[MainViewModel::class.java]

        viewModel.getUserSetting().observe(this, { dataUser ->
            user = dataUser
            with(binding) {
                headerName.text = dataUser.name
                headerDate.text = dateFormat.format(dayEpoch)
                todayActivity.text = resources.getString(R.string.your_today_s_activities, activityFormat.format(dayEpoch))
            }

            userId = if (dataUser.email.isNotEmpty()) viewModel.getUserIdByEmail(dataUser.email) else 0

            viewModel.dayEpoch.observe(this, {
                it.getContentIfNotHandled()?.let { epoch ->
                    viewModel.getUserMedicine(userId, OffsetDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.ofHours(7)).minusHours(7L)).observe(this, { listMedicine ->
                        showMedicine(listMedicine)
                    })
                }
            })
        })

        binding.datePicker.setOnClickListener {
            val calendar = Calendar.getInstance()
            val start = OffsetDateTime.of(
                calendar.get(Calendar.YEAR) - 0,
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH) - 0,
                0,
                0,
                0,
                0,
                ZoneOffset.UTC)
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
                viewModel.setDayEpoch(it)
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

    private fun showMedicine(listMedicine: List<UserMedicine>) {
        binding.todayMedicine.text = resources.getString(R.string.medicine, listMedicine.size)
        if (listMedicine.isNotEmpty()) {
            val adapter = MedicineAdapter(listMedicine)
            binding.listMedicine.visibility = View.VISIBLE
            binding.listMedicine.adapter = adapter
            adapter.setOnCardClickCallback(
                object : MedicineAdapter.OnCardClickCallback {
                    override fun onCardClicked(data: UserMedicine) {
                        showDetailMedicine(data)
                    }
                }
            )
        } else {
            binding.listMedicine.visibility = View.INVISIBLE
        }
    }

    private fun showDetailMedicine(medicine: UserMedicine) {
        val intent = Intent(this@MainActivity, EditMedicineActivity::class.java)
        intent.putExtra(EditMedicineActivity.EXTRA_MEDICINE, medicine.detailMedicine)
        startActivity(intent)
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }
}