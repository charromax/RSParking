package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import com.example.rsparking.data.model.Driver
import com.example.rsparking.util.Constants.TABLE_DRIVERS

@Dao
interface DriverDAO {

    @Query("SELECT * FROM $TABLE_DRIVERS ORDER BY id")
    fun getAllDrivers(): LiveData<List<Driver>>

    @Query("SELECT * FROM $TABLE_DRIVERS WHERE id= :key")
    fun getDriver(key: Int): Driver?

    @Insert(onConflict= OnConflictStrategy.ABORT)
    suspend fun insert(driver: Driver)

    @Update
    suspend fun update(driver: Driver)

    @Query("DELETE FROM $TABLE_DRIVERS")
    suspend fun deleteAllDrivers()

    @Delete
    suspend fun delete(driver: Driver)
}