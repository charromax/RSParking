package com.example.rsparking.ui.driver

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


class DriverViewModel(application: Application): AndroidViewModel(application) {
    private val database: DriverDAO = RSParkingDatabase.getInstance(application).driverDAO
    private val repo: DriverRepository
    private val formatter= SimpleDateFormat(FOR_SQL)

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

    init {
        repo = DriverRepository(database)
        allDrivers= repo.allDrivers
    }

    fun saveDriver() {
        val newDriver= setNewDriver()
        uiScope.launch {
            if (newDriver != null) {
                repo.saveNewDriverToDatabase(newDriver)
            }
        }
    }
    fun updateDriver() {
        uiScope.launch {
            repo.UpdateDriverToDatabase(_driver.value!!)
        }
    }
    private fun setNewDriver(): Driver{
        return Driver(
            0,
            driver.value!!.name,
            driver.value!!.lastName,
            driver.value!!.phone,
            driver.value!!.eMail,
            driver.value!!.image,
            formatter.format(Date())
        )
    }


    fun deleteDriver() {
        //TODO: implementar delete
    }

    fun getDriver(key: Int) {
        uiScope.launch {
            _driver.value= repo.getDriverFromDatabase(key)
        }
    }





}