package com.example.rsparking.ui.driver

import android.app.Application
import android.text.TextWatcher
import androidx.lifecycle.*
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.repo.DriverRepository
import kotlinx.coroutines.*


class DriverViewModel(val repo: DriverRepository,
                      application: Application): AndroidViewModel(application) {

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
    private val allDrivers= repo.getAllDriversFromDatabase()
    // eventos
    private val _eventSaveNewDriver= MutableLiveData<Unit>()
    val eventSaveNewDriver: LiveData<Unit>
        get() = _eventSaveNewDriver

    private val _eventUpdateDriver= MutableLiveData<Unit>()
    val eventUpdateDriver: LiveData<Unit>
        get() = _eventUpdateDriver

    fun saveDriver() {

    }

    fun getDriver(key: Int) {
        uiScope.launch {
            _driver.value= repo.getDriverFromDatabase(key)
        }
    }





}