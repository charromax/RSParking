package com.example.rsparking.ui.driver.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.repo.DriverRepository
import com.example.rsparking.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class DriverListViewModel(application: Application): AndroidViewModel(application) {
    private val database: DriverDAO = RSParkingDatabase.getInstance(application).driverDAO
    private val repo: DriverRepository

    private var viewModelJob= Job()
    private val uiScope= CoroutineScope(Dispatchers.Main + viewModelJob)

    val allDrivers= database.getAllDrivers()

    private val _navigateToAddEditFragment= MutableLiveData<Boolean>()
    val navigateToAddEditFragment: LiveData<Boolean>
        get() = _navigateToAddEditFragment


    init {
        repo= DriverRepository(database)
    }

    fun doneNavigating() {
        _navigateToAddEditFragment.value= null
    }

    fun onDriverSelected(){
        TODO("implementar onitemselectedclick")
    }

    fun onFabClicked() {
        _navigateToAddEditFragment.value= true
    }

    fun deleteDriver(driver: Driver) {
        uiScope.launch {
            repo.DeleteDriverFromDatabase(driver)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}