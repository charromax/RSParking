package com.example.rsparking.ui.client.list

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

class ClientListViewModel(application: Application): AndroidViewModel(application) {
    private val database: ClientDAO = RSParkingDatabase.getInstance(application).clientDAO
    private val repo: ClientRepository

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allClients = database.getAllClients()

    private val _navigateToAddEditFragment = MutableLiveData<Boolean>()
    val navigateToAddEditFragment: LiveData<Boolean>
        get() = _navigateToAddEditFragment

    private val _navigateToAddEditFragmentWithClient = MutableLiveData<Client>()
    val navigateToAddEditFragmentWithClient: LiveData<Client>
        get() = _navigateToAddEditFragmentWithClient

    private val _doneDeletingItem = MutableLiveData<Boolean>()
    val doneDeletingItem: LiveData<Boolean>
        get() = _doneDeletingItem

    private var undoItem = Client()


    init {
        repo = ClientRepository(database)
    }

    fun onFabClicked() {
        _navigateToAddEditFragment.value = true
    }

    fun doneNavigating() {
        _navigateToAddEditFragment.value = null
    }


    fun onListItemClicked(client: Client) {
        _navigateToAddEditFragmentWithClient.value = client
    }

    fun doneNavigatingWithID() {
        _navigateToAddEditFragmentWithClient.value = null
    }


    fun onListItemSwipeRight() {
        _doneDeletingItem.value = null
    }

    private fun doneDeletingItem() {
        _doneDeletingItem.value = true
    }

    fun onConfirmDelete(position: Int) {
        allClients.value?.let {
            val clientToDelete = it[position]
            deleteClient(clientToDelete)
        }
    }

    private fun deleteClient(client: Client) {
        uiScope.launch {
            repo.deleteClientFromDatabase(client)
        }
        doneDeletingItem()
    }

    fun putBackIntoList() {
        uiScope.launch {
            repo.saveNewClient(undoItem)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}