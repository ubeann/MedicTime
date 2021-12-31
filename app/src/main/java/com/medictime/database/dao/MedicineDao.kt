package com.medictime.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.medictime.entity.Medicine
import com.medictime.entity.relation.UserMedicine
import java.time.OffsetDateTime

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg medicine: Medicine)

    @Delete
    fun delete(vararg medicine: Medicine)

    @Transaction
    @Query("SELECT * from `medicine` WHERE user_id = :userId AND DATETIME(date_time) >= DATETIME(:dateTime) ORDER BY DATETIME(date_time) ASC")
    fun getUserMedicine(userId: Int, dateTime: OffsetDateTime): LiveData<List<UserMedicine>>

    @Query("SELECT * from `medicine` WHERE user_id = :userId ORDER BY id DESC LIMIT 1")
    suspend fun getLastMedicineUser(userId: Int): Medicine
}