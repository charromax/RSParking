package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*

import com.example.rsparking.data.model.Client

import com.example.rsparking.util.Constants

@Dao
interface ClientDAO {

    @Query("SELECT * FROM ${Constants.TABLE_CLIENTS} ORDER BY id")
    fun getAllClients(): LiveData<MutableList<Client>>

    @Query("SELECT * FROM ${Constants.TABLE_CLIENTS} WHERE id= :key")
    fun getClient(key: String): Client?

    @Query("SELECT * FROM ${Constants.TABLE_CLIENTS} WHERE plateNumber LIKE :key")
    fun getClientWithPlate(key: String): Client?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(client: Client)

    @Update
    suspend fun update(client: Client)

    @Query("DELETE FROM ${Constants.TABLE_CLIENTS}")
    suspend fun deleteAllDrivers()

    @Delete
    suspend fun delete(client: Client)
}