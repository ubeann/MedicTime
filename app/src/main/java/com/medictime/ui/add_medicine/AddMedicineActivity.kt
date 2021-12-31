package com.medictime.ui.add_medicine

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.medictime.App
import com.medictime.R
import com.medictime.databinding.ActivityAddMedicineBinding
import com.medictime.preferences.UserPreferences
import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*

class AddMedicineActivity : AppCompatActivity() {
    private var _binding: ActivityAddMedicineBinding? = null
    private val binding get() = _binding!!
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)
    private val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault())
    private var dayEpoch: Long = OffsetDateTime.now().toInstant().toEpochMilli()
    private var timeEpoch: OffsetTime = OffsetTime.now()
    private var userId: Int = 0
    private lateinit var preferences: UserPreferences
    private lateinit var viewModel: AddMedicineViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityAddMedicineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferences = UserPreferences.getInstance(dataStore)
        viewModel = ViewModelProvider(this, AddMedicineViewModelFactory(application, preferences))[AddMedicineViewModel::class.java]

        viewModel.getUserSetting().observe(this, { dataUser ->
            userId = if (dataUser.email.isNotEmpty()) viewModel.getUserIdByEmail(dataUser.email) else 0
        })

        with(binding) {
            inputDate.editText?.setText(dateFormat.format(dayEpoch))
            inputTime.editText?.setText(timeEpoch.format(timeFormat))
            cardMedicine.time.text = timeEpoch.format(timeFormat)

            with(inputType) {
                val items = listOf("Tablet", "Syrup", "Capsule")
                val adapter = ArrayAdapter(binding.root.context, R.layout.list_item, items)
                (editText as? AutoCompleteTextView)?.setAdapter(adapter)
            }
        }

        binding.inputName.editText?.setOnFocusChangeListener { _, focus ->
            binding.cardMedicine.name.text = when {
                (binding.inputAmount.editText?.text?.isNotEmpty() == true) and !focus -> "${binding.inputAmount.editText?.text}x ${binding.inputName.editText?.text}"
                (binding.inputAmount.editText?.text?.isEmpty() == true) and !focus -> binding.inputName.editText?.text
                else -> binding.cardMedicine.name.text
            }
        }

        binding.inputType.editText?.setOnFocusChangeListener { _, _ ->
            Glide.with(binding.root.context)
                .load(
                    when (binding.inputType.editText?.text.toString()) {
                        "Tablet" -> R.drawable.ic_medicine_tablet
                        "Syrup" -> R.drawable.ic_medicine_syrup
                        "Capsule" -> R.drawable.ic_medicine_capsule
                        else -> R.drawable.logo
                    }
                )
                .into(binding.cardMedicine.image)
        }

        binding.inputDescription.editText?.setOnFocusChangeListener { _, focus ->
            binding.cardMedicine.description.text = when {
                (binding.inputDescription.editText?.text?.isNotEmpty() == true) and !focus -> binding.inputDescription.editText?.text
                (binding.inputDescription.editText?.text?.isEmpty() == true) and !focus -> "description"
                else -> binding.cardMedicine.description.text
            }
        }

        binding.inputAmount.editText?.setOnFocusChangeListener { _, focus ->
            binding.cardMedicine.name.text = when {
                (binding.inputName.editText?.text?.isNotEmpty() == true) and (binding.inputAmount.editText?.text?.isNotEmpty() == true) and !focus -> "${binding.inputAmount.editText?.text}x ${binding.inputName.editText?.text}"
                (binding.inputName.editText?.text?.isEmpty() == true) and (binding.inputAmount.editText?.text?.isNotEmpty() == true) and !focus -> "${binding.inputAmount.editText?.text}x Medicine Name"
                (binding.inputName.editText?.text?.isNotEmpty() == true) and (binding.inputAmount.editText?.text?.isEmpty() == true) and !focus -> binding.inputName.editText?.text
                (binding.inputName.editText?.text?.isEmpty() == true) and (binding.inputAmount.editText?.text?.isEmpty() == true) and !focus -> "Medical Name"
                else -> binding.cardMedicine.name.text
            }
        }

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
                binding.inputDate.editText?.setText(dateFormat.format(dayEpoch))
            }
        }

        binding.timePicker.setOnClickListener {
            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(timeEpoch.hour)
                .setMinute(timeEpoch.minute)
                .build()
            picker.show(this.supportFragmentManager, TIME_PICKER_TAG)
            picker.addOnPositiveButtonClickListener {
                timeEpoch = OffsetTime.of(picker.hour, picker.minute, 0, 0, OffsetTime.now().offset)
                with(binding) {
                    inputTime.editText?.setText(timeEpoch.format(timeFormat))
                    cardMedicine.time.text = timeEpoch.format(timeFormat)
                }
            }
        }

        binding.btnAdd.setOnClickListener {
            closeKeyboard()

            with(binding) {
                val isNameFilled = isInputFilled(inputName, "Please fill name of medicine")
                val isTypeFilled = isInputFilled(inputType, "Please select type of medicine")
                val isDescriptionFilled = isInputFilled(inputDescription, "Please fill description of medicine")
                val isDateFilled = isInputFilled(inputDate, "Please select date of reminder")
                val isTimeFilled = isInputFilled(inputTime, "Please select time of reminder")
                val isAmountFilled = isInputFilled(inputAmount, "Please fill amount of medicine")

                if (isNameFilled and isTypeFilled and isDescriptionFilled and isDateFilled and isTimeFilled and isAmountFilled) {
                    viewModel.addMedicine(
                        context = this@AddMedicineActivity,
                        user_id = userId,
                        medicine_name = inputName.editText?.text.toString(),
                        medicine_type = inputType.editText?.text.toString(),
                        medicine_description = inputDescription.editText?.text.toString(),
                        medicine_date = OffsetDateTime.ofInstant(Instant.ofEpochMilli(dayEpoch), ZoneId.systemDefault()),
                        medicine_time = timeEpoch,
                        medicine_amount = inputAmount.editText?.text.toString().toInt()
                    )
                    onBackPressed()
                }
            }
        }

        binding.btnBack.setOnClickListener {
            closeKeyboard()
            onBackPressed()
        }

        viewModel.notificationText.observe(this, {
            it.getContentIfNotHandled()?.let { text ->
                showSnackBar(text)
            }
        })
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

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
        private const val TIME_PICKER_TAG = "TimePicker"
    }
}