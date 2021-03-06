package com.medictime.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.medictime.database.dao.*
import com.medictime.entity.Medicine
import com.medictime.entity.User

@Database(
    version = 1,
    entities = [
        User::class,
        Medicine::class
    ],
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun medicineDao(): MedicineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java, "database.db"
                    )
                    .fallbackToDestructiveMigration()
                    .build()
                }
            }
            return INSTANCE as AppDatabase
        }
    }
}