package com.example.rsparking.ui.dropoffs.addedit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.*
import com.example.rsparking.data.model.*
import com.example.rsparking.data.repo.ClientRepository
import com.example.rsparking.data.repo.DropOffRepository
import com.example.rsparking.data.repo.HiredPlanRepository
import com.example.rsparking.data.repo.VehicleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AddEditDropOffViewModel(
    dropOffID: String?,
    application: Application
) : AndroidViewModel(application) {

    private val dropOffDAO: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val clientDAO: ClientDAO = RSParkingDatabase.getInstance(application).clientDAO
    private val vehicleDAO: VehicleDAO = RSParkingDatabase.getInstance(application).vehicleDAO
    private val hiredPlanDAO: HiredPlanDAO = RSParkingDatabase.getInstance(application).hiredPlanDAO
    private val dropOffRepository: DropOffRepository
    private val clientRepository: ClientRepository
    private val vehicleRepository: VehicleRepository
    private val hiredPlanRepository: HiredPlanRepository
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)
    val feeTypes =
        ArrayList<String>()
    val serviceTypes =
        ArrayList<String>()


    // drop off info
    val currentDropOff = MutableLiveData<DropOff>()

    private val _currentClient = MutableLiveData<Client>()
    val currentClient: LiveData<Client>
        get() = _currentClient

    private val _currentVehicle = MutableLiveData<Vehicle>()
    val currentVehicle: LiveData<Vehicle>
        get() = _currentVehicle

    private val _currentHiredPlan = MutableLiveData<HiredPlan>()
    val currentHiredPlan: LiveData<HiredPlan>
        get() = _currentHiredPlan

    var allClients: LiveData<MutableList<Client>>
    var allVehicles: LiveData<MutableList<Vehicle>>

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


    var isCrew = MutableLiveData<Boolean>()
    //TODO data binding checkbox

    init {
        dropOffRepository = DropOffRepository(dropOffDAO)
        clientRepository = ClientRepository(clientDAO)
        vehicleRepository = VehicleRepository(vehicleDAO)
        hiredPlanRepository = HiredPlanRepository(hiredPlanDAO)
        dropOffID?.let {
            getDropOff(it)
        }
        setSpinnerArrays()
        allClients = clientRepository.allClients
        allVehicles = vehicleRepository.allVehicles
    }

    private fun setSpinnerArrays() {
        for (plan in PayPlan.values()) {
            feeTypes.add(plan.code)
        }
        for (service in ServiceType.values()) {
            serviceTypes.add(service.code)
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

    fun getCurrentClient(key: String) {
        uiScope.launch {
            _currentClient.value = clientRepository.getClientFromDatabase(key)
        }
    }

    fun getCurrentHiredPlan(clientID: String) {
        uiScope.launch {
            _currentHiredPlan.value = hiredPlanRepository.getCurrentPlanForClientID(clientID)
        }
    }


    fun doneUpdating() {
        _updateDropOffEvent.value = null
    }


    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}