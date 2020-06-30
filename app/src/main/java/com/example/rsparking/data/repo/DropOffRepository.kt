package com.example.rsparking.data.repo

import com.example.rsparking.data.RoomDatabase.DropOffDAO
import com.example.rsparking.data.model.DropOff
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DropOffRepository(val database: DropOffDAO) {
    val allDropOffsMinusPickedUp = database.getAllDropOffsExceptPickedUp()
    val allDropOffsOnlyPickedUp = database.getAllDropOffsPickedUp()

    suspend fun getDropOffsForClientID(key: String): List<DropOff> {
        return withContext(Dispatchers.IO) {
            val dropOffsForClient = database.getAllDropOffsForClientID(key)
            dropOffsForClient
        }
    }

    suspend fun getDropOffByID(key: String): DropOff? {
        return withContext(Dispatchers.IO) {
            val foundDropOff = database.getDropOff(key)
            foundDropOff
        }
    }

    suspend fun getDropOffByDateOut(key: String): List<DropOff> {
        return withContext(Dispatchers.IO) {
            val foundDropOffs = database.getDropOffByDateOut(key)
            foundDropOffs
        }
    }

    suspend fun saveNewDropOff(dropOff: DropOff) {
        withContext(Dispatchers.IO) {
            database.insert(dropOff)
        }
    }

    suspend fun UpdateDropOff(dropOff: DropOff) {
        withContext(Dispatchers.IO) {
            database.update(dropOff)
        }
    }

    suspend fun DeleteDropOffFromDatabase(dropOff: DropOff) {
        withContext(Dispatchers.IO) {
            database.delete(dropOff)
        }
    }

    suspend fun deleteAllDropOffPickedUp() {
        withContext(Dispatchers.IO) {
            database.deleteAllDropOffsPickedUp()
        }
    }
}