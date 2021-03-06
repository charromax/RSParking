package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rsparking.data.model.Driver
import com.example.rsparking.util.Constants.TABLE_DRIVERS

@Dao
interface DriverDAO {

    @Query("SELECT * FROM $TABLE_DRIVERS ORDER BY id")
    fun getAllDrivers(): LiveData<MutableList<Driver>>

    @Query("SELECT * FROM $TABLE_DRIVERS WHERE id= :key")
    fun getDriver(key: String): Driver?

    @Insert(onConflict= OnConflictStrategy.REPLACE)
    suspend fun insert(driver: Driver)

    @Update
    suspend fun update(driver: Driver)

    @Query("DELETE FROM $TABLE_DRIVERS")
    suspend fun deleteAllDrivers()

    @Delete
    suspend fun delete(driver: Driver)
}