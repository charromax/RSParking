package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.util.Constants

@Dao
interface DropOffDAO {

    @Query("SELECT * FROM ${Constants.TABLE_DROPOFFS} where isPickedUp=0 ORDER BY dateOUT")
    fun getAllDropOffsExceptPickedUp(): LiveData<MutableList<DropOff>>

    @Query("SELECT * FROM ${Constants.TABLE_DROPOFFS} where isPickedUp=1 ORDER BY dateOUT")
    fun getAllDropOffsPickedUp(): LiveData<MutableList<DropOff>>

    @Query("SELECT * FROM ${Constants.TABLE_DROPOFFS} where clientID=:key ORDER BY dateOUT")
    fun getAllDropOffsForClientID(key: String): List<DropOff>

    @Query("SELECT * FROM ${Constants.TABLE_DROPOFFS} WHERE id=:key ORDER BY dateOUT")
    fun getDropOff(key: String): DropOff?

    @Query("SELECT * FROM ${Constants.TABLE_DROPOFFS} WHERE dateOUT=:key ORDER BY dateOUT")
    fun getDropOffByDateOut(key: String): List<DropOff>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(dropOff: DropOff)

    @Update
    suspend fun update(dropOff: DropOff)

    @Query("DELETE FROM ${Constants.TABLE_DROPOFFS}")
    suspend fun deleteAllDropOffs()

    @Delete
    suspend fun delete(dropOff: DropOff)
}