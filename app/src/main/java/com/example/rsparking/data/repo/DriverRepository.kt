package com.example.rsparking.data.repo

import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.model.Driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DriverRepository(val database: DriverDAO) {
    val allDrivers= database.getAllDrivers()

    suspend fun getDriverFromDatabase(key: String): Driver? {
        return withContext(Dispatchers.IO) {
            val foundDriver = database.getDriver(key)
            foundDriver
        }
    }

    suspend fun saveNewDriverToDatabase(driver: Driver) {
        withContext(Dispatchers.IO) {
            database.insert(driver)
        }
    }

    suspend fun UpdateDriverToDatabase(driver: Driver) {
        withContext(Dispatchers.IO) {
            database.update(driver)
        }
    }

    suspend fun DeleteDriverFromDatabase(driver: Driver) {
        withContext(Dispatchers.IO) {
            database.delete(driver)
        }
    }

}