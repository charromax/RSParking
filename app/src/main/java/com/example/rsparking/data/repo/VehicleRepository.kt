package com.example.rsparking.data.repo

import com.example.rsparking.data.RoomDatabase.VehicleDAO
import com.example.rsparking.data.model.Vehicle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class VehicleRepository(val database: VehicleDAO) {
    val allVehicles = database.getAllVehicles()

    suspend fun getVehicleWithClientID(key: String): Vehicle? {
        return withContext(Dispatchers.IO) {
            val foundVehicle = database.getVehicleWithClientID(key)
            foundVehicle
        }
    }

    suspend fun getVehicleWithPlate(key: String): Vehicle? {
        return withContext(Dispatchers.IO) {
            val foundVehicle = database.getVehicleWithPlate(key)
            foundVehicle
        }
    }

    suspend fun saveNewVehicle(vehicle: Vehicle) {
        withContext(Dispatchers.IO) {
            database.insert(vehicle)
        }
    }

    suspend fun updateVehicle(vehicle: Vehicle) {
        withContext(Dispatchers.IO) {
            database.update(vehicle)
        }
    }

    suspend fun deleteVehicle(vehicle: Vehicle) {
        withContext(Dispatchers.IO) {
            database.delete(vehicle)
        }
    }
}