package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.util.Constants

@Dao
interface DropOffDAO {

    @Query("SELECT * FROM ${Constants.TABLE_DROPOFFS} ORDER BY id")
    fun getAllDropOffs(): LiveData<MutableList<DropOff>>

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