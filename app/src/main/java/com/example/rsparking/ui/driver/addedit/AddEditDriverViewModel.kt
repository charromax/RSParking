package com.example.rsparking.ui.driver.addedit

import android.app.Application
import android.text.TextWatcher
import androidx.lifecycle.*
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.repo.DriverRepository
import com.example.rsparking.util.Constants.FOR_SQL
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class AddEditDriverViewModel(application: Application): AndroidViewModel(application) {
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

    private val _saveDriverEvent= MutableLiveData<Boolean>()
    val saveDriverEvent: LiveData<Boolean>
        get() = _saveDriverEvent


    init {
        repo = DriverRepository(database)
    }

    fun saveDriver(newDriver: Driver) {
        uiScope.launch {
            repo.saveNewDriverToDatabase(newDriver)
        }
    }
    fun updateDriver(newDriver: Driver) {
        uiScope.launch {
            repo.UpdateDriverToDatabase(_driver.value!!)
        }
    }

    fun doneSaving() {
        _saveDriverEvent.value= null
    }

    fun onSaveEvent(){
        _saveDriverEvent.value= true
    }




    fun getDriver(key: Int) {
        uiScope.launch {
            _driver.value= repo.getDriverFromDatabase(key)
        }
    }





}