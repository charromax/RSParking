package com.example.rsparking.ui.dropoffs.addedit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.R
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
import java.util.*
import kotlin.collections.ArrayList


class AddEditDropOffViewModel(
    dropOffID: String?, application: Application
) : AndroidViewModel(application) {
    private val dropOffDAO: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val clientDAO: ClientDAO = RSParkingDatabase.getInstance(application).clientDAO
    private val dropOffRepository: DropOffRepository
    private val clientRepository: ClientRepository
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val feeTypes =
        ArrayList<String>(Arrays.asList(*application.resources.getStringArray(R.array.fee_types)))
    val serviceTypes =
        ArrayList<String>(Arrays.asList(*application.resources.getStringArray(R.array.service_types)))


    // drop off info
    val currentDropOff = MutableLiveData<DropOff>()


    private val _saveDropOffEvent = MutableLiveData<Boolean>()
    val saveDropOffEvent: LiveData<Boolean>
        get() = _saveDropOffEvent

    private val _updateDropOffEvent = MutableLiveData<Boolean>()
    val updateDropOffEvent: LiveData<Boolean>
        get() = _updateDropOffEvent

    val _saveNewClient = MutableLiveData<Boolean>()
    val saveNewClient: LiveData<Boolean>
        get() = _saveNewClient
    //TODO data binding checkbox

    init {
        dropOffRepository = DropOffRepository(dropOffDAO)
        clientRepository = ClientRepository(clientDAO)
        resetCheckBox()
        dropOffID?.let {
            getDropOff(it)
        }
    }

    fun saveDropOff(dropOff: DropOff, client: Client?) {
        uiScope.launch {
            client?.let {
                clientRepository.saveNewClient(it)
                Log.i("TAG", "saved client: ${it.name} ")
            }
        }
        uiScope.launch {
            dropOffRepository.saveNewDropOff(dropOff)
        }
    }

    fun updateDropOff() {
        uiScope.launch {
            currentDropOff.value?.let { dropOffRepository.UpdateDropOff(it) }
        }
    }

    fun doneSaving() {
        _saveDropOffEvent.value = null
    }

    fun onSaveEvent() {
        _saveDropOffEvent.value = true
    }

    fun getDropOff(key: String) {
        uiScope.launch {
            currentDropOff.value = dropOffRepository.getDropOffByID(key)
        }
    }

    fun onUpdateEvent() {
        _updateDropOffEvent.value = true
    }

    fun doneUpdating() {
        _updateDropOffEvent.value = null
    }

    fun onCheckBoxClick() {
        _saveNewClient.value = true
    }

    fun resetCheckBox() {
        _saveNewClient.value = false
    }


    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}