package com.example.rsparking.ui.client.addedit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.ClientDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.Client
import com.example.rsparking.data.repo.ClientRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AddEditClientViewModel(
    private val clientID: String?, application: Application
) : AndroidViewModel(application) {
    private val database: ClientDAO = RSParkingDatabase.getInstance(application).clientDAO
    private val repo: ClientRepository
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    // driver personal info
    val currentClient = MutableLiveData<Client>()


    private val _saveClientEvent = MutableLiveData<Boolean>()
    val saveClientEvent: LiveData<Boolean>
        get() = _saveClientEvent

    private val _updateClientEvent = MutableLiveData<Boolean>()
    val updateClientEvent: LiveData<Boolean>
        get() = _updateClientEvent


    init {
        repo = ClientRepository(database)
        clientID?.let {
            getClient(it)
        }
    }

    fun saveClient(client: Client) {
        uiScope.launch {
            repo.saveNewClient(client)
        }
    }

    fun updateClient() {
        uiScope.launch {
            currentClient.value?.let { repo.UpdateClient(it) }
        }
    }

    fun doneSaving() {
        _saveClientEvent.value = null
    }

    fun onSaveEvent() {
        _saveClientEvent.value = true
    }

    fun getClient(key: String) {
        uiScope.launch {
            currentClient.value = repo.getClientFromDatabase(key)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    fun onUpdateEvent() {
        _updateClientEvent.value = true
    }

    fun doneUpdating() {
        _updateClientEvent.value = null
    }


}