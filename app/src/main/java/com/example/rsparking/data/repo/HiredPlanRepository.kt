package com.example.rsparking.data.repo

import com.example.rsparking.data.RoomDatabase.HiredPlanDAO
import com.example.rsparking.data.model.HiredPlan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HiredPlanRepository(val database: HiredPlanDAO) {
    val allHiredPlans = database.getAllHiredPlans()

    suspend fun getAllActivePlans(key: String): List<HiredPlan> {
        return withContext(Dispatchers.IO) {
            return@withContext database.getAllActivePlans(key)
        }
    }

    suspend fun getAllPlansForClientID(key: String): List<HiredPlan> {
        return withContext(Dispatchers.IO) {
            return@withContext database.getAllPlansForClientID(key)
        }
    }

    suspend fun getCurrentPlanForClientID(key: String): HiredPlan? {
        return withContext(Dispatchers.IO) {
            return@withContext database.getCurrentPlanForClientID(key)
        }
    }

    suspend fun hireNewPlan(hiredPlan: HiredPlan) {
        withContext(Dispatchers.IO) {
            database.insert(hiredPlan)
        }
    }

    suspend fun renewPlan(hiredPlan: HiredPlan) {
        withContext(Dispatchers.IO) {
            database.update(hiredPlan)
        }
    }

    suspend fun deletePlan(hiredPlan: HiredPlan) {
        withContext(Dispatchers.IO) {
            database.delete(hiredPlan)
        }
    }
}