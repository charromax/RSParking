package com.example.rsparking.data.repo

import androidx.lifecycle.LiveData
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.model.Driver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext

class DriverRepository(val database: DriverDAO) {

    suspend fun getDriverFromDatabase(key: Int): Driver? {
        return withContext(Dispatchers.IO) {
            val foundDriver= database.getDriver(key)
            foundDriver
        }
    }

    fun getAllDriversFromDatabase(): LiveData<List<Driver>> {
        return database.getAllDrivers()
    }


}