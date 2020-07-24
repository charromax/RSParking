package com.example.rsparking.data.RoomDatabase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.rsparking.data.model.HiredPlan
import com.example.rsparking.util.Constants

@Dao
interface HiredPlanDAO {
    @Query("SELECT * FROM ${Constants.TABLE_HIRED_PLAN} ORDER BY clientID")
    fun getAllHiredPlans(): LiveData<MutableList<HiredPlan>>

    @Query("SELECT * FROM ${Constants.TABLE_HIRED_PLAN} where endDate > :currentDate ORDER BY clientID")
    fun getAllActivePlans(currentDate: String): List<HiredPlan>

    @Query("SELECT * FROM ${Constants.TABLE_HIRED_PLAN} where clientID=:key ORDER BY endDate DESC LIMIT 1")
    fun getCurrentPlanForClientID(key: String): HiredPlan?

    @Query("SELECT * FROM ${Constants.TABLE_HIRED_PLAN} WHERE id=:key ORDER BY endDate")
    fun getAllPlansForClientID(key: String): List<HiredPlan>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(hiredPlan: HiredPlan)

    @Update
    suspend fun update(hiredPlan: HiredPlan)

    @Query("DELETE FROM ${Constants.TABLE_HIRED_PLAN} WHERE endDate < :currentDate")
    suspend fun deleteAllOldPlans(currentDate: String)

    @Delete
    suspend fun delete(hiredPlan: HiredPlan)
}