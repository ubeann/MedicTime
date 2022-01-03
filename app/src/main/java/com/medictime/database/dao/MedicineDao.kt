package com.medictime.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.medictime.entity.Medicine
import com.medictime.entity.relation.UserMedicine
import java.time.OffsetDateTime

@Dao
interface MedicineDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(medicine: Medicine) : Long

    @Update
    fun update(vararg medicine: Medicine)

    @Delete
    fun delete(vararg medicine: Medicine)

    @Transaction
    @Query("SELECT * from `medicine` WHERE `user_id` = :userId AND DATETIME(`date_time`) >= DATETIME(:start) AND DATETIME(`date_time`) <= DATETIME(:close) ORDER BY DATETIME(`date_time`) ASC")
    fun getUserMedicine(userId: Int, start: OffsetDateTime, close: OffsetDateTime): LiveData<List<UserMedicine>>
}