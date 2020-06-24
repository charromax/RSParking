package com.example.rsparking.ui.driver.addedit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.repo.DriverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class AddEditDriverViewModel(
    private val driverID: String?, application: Application
) : AndroidViewModel(application) {
    private val database: DriverDAO = RSParkingDatabase.getInstance(application).driverDAO
    private val repo: DriverRepository
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    // driver personal info
    val currentdriver = MutableLiveData<Driver>()


    private val _saveDriverEvent = MutableLiveData<Boolean>()
    val saveDriverEvent: LiveData<Boolean>
        get() = _saveDriverEvent

    private val _updateDriverEvent = MutableLiveData<Boolean>()
    val updateDriverEvent: LiveData<Boolean>
        get() = _updateDriverEvent

    private val _openCameraEvent = MutableLiveData<Boolean>()
    val openCameraEvent: LiveData<Boolean>
        get() = _openCameraEvent


    init {
        repo = DriverRepository(database)
        driverID?.let {
            getDriver(it)
        }
    }

    fun saveDriver(newDriver: Driver) {
        uiScope.launch {
            repo.saveNewDriverToDatabase(newDriver)
        }
    }

    fun updateDriver() {
        uiScope.launch {
            currentdriver.value?.let { repo.UpdateDriverToDatabase(it) }
        }
    }

    fun doneSaving() {
        _saveDriverEvent.value = null
    }

    fun onSaveEvent() {
        _saveDriverEvent.value = true
    }

    fun onOpenCameraEvent() {
        _openCameraEvent.value = true
    }

    fun doneWithCamera() {
        _openCameraEvent.value = null
    }

    fun getDriver(key: String) {
        uiScope.launch {
            currentdriver.value = repo.getDriverFromDatabase(key)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    fun onUpdateEvent() {
        _updateDriverEvent.value = true
    }

    fun doneUpdating() {
        _updateDriverEvent.value = null
    }


}