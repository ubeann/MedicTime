package com.medictime.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.medictime.database.AppDatabase
import com.medictime.database.dao.MedicineDao
import com.medictime.entity.Medicine
import com.medictime.entity.relation.UserMedicine
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MedicineRepository(application: Application) {
    private val mMedicineDao: MedicineDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)
        mMedicineDao = db.medicineDao()
    }

    fun getUserMedicine(userId: Int) : LiveData<List<UserMedicine>> = mMedicineDao.getUserMedicine(userId)

    fun insert(vararg medicine: Medicine) {
        executorService.execute { mMedicineDao.insert(*medicine) }
    }

    fun delete(vararg medicine: Medicine) {
        executorService.execute { mMedicineDao.delete(*medicine) }
    }
}