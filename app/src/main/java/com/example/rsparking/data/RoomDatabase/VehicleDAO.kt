package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rsparking.data.model.Vehicle
import com.example.rsparking.util.Constants

@Dao
interface VehicleDAO {

    @Query("SELECT * FROM ${Constants.TABLE_VEHICLES} ORDER BY plateNumber")
    fun getAllVehicles(): LiveData<MutableList<Vehicle>>

    @Query("SELECT * FROM ${Constants.TABLE_VEHICLES} WHERE clientID= :key")
    fun getVehicleWithClientID(key: String): Vehicle?

    @Query("SELECT * FROM ${Constants.TABLE_VEHICLES} WHERE plateNumber LIKE :key")
    fun getVehicleWithPlate(key: String): Vehicle?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: Vehicle)

    @Update
    suspend fun update(vehicle: Vehicle)

    @Query("DELETE FROM ${Constants.TABLE_VEHICLES}")
    suspend fun deleteAllVehicles()

    @Delete
    suspend fun delete(vehicle: Vehicle)
}