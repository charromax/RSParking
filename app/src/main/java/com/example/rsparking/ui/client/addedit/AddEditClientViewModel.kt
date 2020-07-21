package com.example.rsparking.ui.client.addedit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.ClientDAO
import com.example.rsparking.data.RoomDatabase.DropOffDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.Client
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.data.repo.ClientRepository
import com.example.rsparking.data.repo.DropOffRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AddEditClientViewModel(
    private val clientID: String?, application: Application
) : AndroidViewModel(application) {
    private val database: ClientDAO = RSParkingDatabase.getInstance(application).clientDAO
    private val dropOffDB: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val repo: ClientRepository
    private val dropRepo: DropOffRepository
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    // client personal info
    var currentClient = MutableLiveData<Client>()


    private val _saveClientEvent = MutableLiveData<Boolean>()
    val saveClientEvent: LiveData<Boolean>
        get() = _saveClientEvent

    private val _updateClientEvent = MutableLiveData<Boolean>()
    val updateClientEvent: LiveData<Boolean>
        get() = _updateClientEvent


    init {
        repo = ClientRepository(database)
        dropRepo = DropOffRepository(dropOffDB)
        clientID?.let {
            getClient(it)
            setClientAVGScore()
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
            setClientAVGScore()
        }

    }

    private fun setClientAVGScore() {
        //TODO improve this
        var scoreList: ArrayList<DropOff>
        var score = 0.0f
        var sum = 0.0f
        currentClient.value?.let {
            uiScope.launch {
                scoreList = ArrayList(dropRepo.getDropOffsForClientID(it.id))
                for (item in scoreList) {
                    sum += item.score
                }
                if (scoreList.isNotEmpty()) {
                    score = sum / scoreList.size
                }
                currentClient.value?.score = score
            }
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