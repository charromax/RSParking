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
    dropOffID: String?,
    application: Application
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
    var allClients = clientDAO.getAllClients()

    private val _saveDropOffEvent = MutableLiveData<Boolean>()
    val saveDropOffEvent: LiveData<Boolean>
        get() = _saveDropOffEvent

    private val _foundClient = MutableLiveData<Boolean>()
    val foundClient: LiveData<Boolean>
        get() = _foundClient

    private val _dateOutClickedEvent = MutableLiveData<Boolean>()
    val dateOutClickedEvent: LiveData<Boolean>
        get() = _dateOutClickedEvent

    private val _updateDropOffEvent = MutableLiveData<Boolean>()
    val updateDropOffEvent: LiveData<Boolean>
        get() = _updateDropOffEvent

    var isChecked = MutableLiveData<Boolean>()

    var isCrew = MutableLiveData<Boolean>()
    //TODO data binding checkbox

    init {
        dropOffRepository = DropOffRepository(dropOffDAO)
        clientRepository = ClientRepository(clientDAO)
        dropOffID?.let {
            getDropOff(it)
        }
        allClients = clientRepository.allClients
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

    fun updateDateOut(date:String) {
        currentDropOff.value?.let { dropOff ->
            dropOff.dateOUT = date
        }
    }
    fun openPicker() {
        _dateOutClickedEvent.value = true
    }
    fun doneWithPicker() {
        _dateOutClickedEvent.value = null
    }


    fun doneSaving() {
        _saveDropOffEvent.value = false
    }

    fun onSaveEvent() {
        _saveDropOffEvent.value = true
    }

    fun getDropOff(key: String) {
        uiScope.launch {
            currentDropOff.value = dropOffRepository.getDropOffByID(key)
        }
    }

    fun doneUpdating() {
        _updateDropOffEvent.value = null
    }

    fun onPlateNumberInput(plate: CharSequence) {
        var client: Client? = null
        uiScope.launch {
            client = clientRepository.getClientWithPlate(plate.toString())
        }
        currentDropOff.value?.let { dropOff ->
            client?.let { client ->
                dropOff.clientID = client.id
                dropOff.clientName = client.name
                dropOff.clientPhone = client.name
                _foundClient.value = true
            }
        }
    }


    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}