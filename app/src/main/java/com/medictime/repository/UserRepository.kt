package com.medictime.repository

import android.app.Application
import com.medictime.database.AppDatabase
import com.medictime.database.dao.UserDao
import com.medictime.entity.User
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = AppDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun getUserByEmail(email: String): User = runBlocking { mUserDao.getUserByEmail(email) }

    fun getUserByName(username: String): User = runBlocking { mUserDao.getUserByName(username) }

    fun isEmailRegistered(email: String): Boolean = runBlocking { mUserDao.isEmailRegistered(email) }

    fun isUsernameRegistered(username: String): Boolean = runBlocking { mUserDao.isUsernameRegistered(username) }

    fun insert(user: User) {
        executorService.execute { mUserDao.insert(user) }
    }

    fun update(user: User) {
        executorService.execute { mUserDao.update(user) }
    }
}