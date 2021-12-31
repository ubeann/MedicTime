package com.medictime.database.dao

import androidx.room.*
import com.medictime.entity.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Query("SELECT * from user WHERE email = :email ORDER BY id ASC LIMIT 1")
    suspend fun getUserByEmail(email: String): User

    @Query("SELECT * from user WHERE username = :username ORDER BY id ASC LIMIT 1")
    suspend fun getUserByName(username: String): User

    @Query("SELECT EXISTS (SELECT * from user WHERE email = :email)")
    suspend fun isEmailRegistered(email: String): Boolean

    @Query("SELECT EXISTS (SELECT * from user WHERE username = :username)")
    suspend fun isUsernameRegistered(username: String): Boolean
}