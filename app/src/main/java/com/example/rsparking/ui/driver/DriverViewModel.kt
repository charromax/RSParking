package com.example.rsparking.ui.driver

import android.app.Application
import android.text.TextWatcher
import androidx.lifecycle.*
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.repo.DriverRepository
import kotlinx.coroutines.*


class DriverViewModel(application: Application): AndroidViewModel(application) {
    private val database: DriverDAO = RSParkingDatabase.getInstance(application).driverDAO
    private val repo: DriverRepository

    private var viewModelJob= Job()
    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    private val uiScope= CoroutineScope(Dispatchers.Main + viewModelJob)
    // driver personal info
    private val _driver= MutableLiveData<Driver>()
    val driver: LiveData<Driver>
        get() = _driver

    val allDrivers: LiveData<MutableList<Driver>>

    // eventos
    private val _eventSaveNewDriver= MutableLiveData<Unit>()
    val eventSaveNewDriver: LiveData<Unit>
        get() = _eventSaveNewDriver

    private val _eventUpdateDriver= MutableLiveData<Unit>()
    val eventUpdateDriver: LiveData<Unit>
        get() = _eventUpdateDriver

    init {
        repo = DriverRepository(database)
        allDrivers= repo.allDrivers
    }

    fun saveDriver() {
        val newDriver= _driver.value
        uiScope.launch {
            if (newDriver != null) {
                repo.saveNewDriverToDatabase(newDriver)
            }
        }
    }

    fun getDriver(key: Int) {
        uiScope.launch {
            _driver.value= repo.getDriverFromDatabase(key)
        }
    }





}