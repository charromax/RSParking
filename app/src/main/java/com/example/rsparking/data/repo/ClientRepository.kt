package com.example.rsparking.data.repo

import com.example.rsparking.data.RoomDatabase.ClientDAO
import com.example.rsparking.data.model.Client
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ClientRepository(val database: ClientDAO) {
    val allClients = database.getAllClients()

    suspend fun getClientFromDatabase(key: String): Client? {
        return withContext(Dispatchers.IO) {
            val foundClient = database.getClient(key)
            foundClient
        }
    }

    suspend fun saveNewClient(client: Client) {
        withContext(Dispatchers.IO) {
            database.insert(client)
        }
    }

    suspend fun UpdateClient(client: Client) {
        withContext(Dispatchers.IO) {
            database.update(client)
        }
    }

    suspend fun DeleteClientFromDatabase(client: Client) {
        withContext(Dispatchers.IO) {
            database.delete(client)
        }
    }
}