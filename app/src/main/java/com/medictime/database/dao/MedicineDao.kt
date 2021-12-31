package com.medictime.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.medictime.entity.Medicine
import com.medictime.entity.relation.UserMedicine

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg medicine: Medicine)

    @Delete
    fun delete(vararg medicine: Medicine)

    @Transaction
    @Query("SELECT * from `medicine` WHERE user_id = :userId AND DATETIME(date_time) >= DATE('now') ORDER BY DATETIME(date_time) ASC")
    fun getUserMedicine(userId: Int): LiveData<List<UserMedicine>>
}