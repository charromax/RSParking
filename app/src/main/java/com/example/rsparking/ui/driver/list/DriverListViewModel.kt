package com.example.rsparking.ui.driver.list

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

class DriverListViewModel(application: Application): AndroidViewModel(application) {
    private val database: DriverDAO = RSParkingDatabase.getInstance(application).driverDAO
    private val repo: DriverRepository

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allDrivers = database.getAllDrivers()

    private val _navigateToAddEditFragment = MutableLiveData<Boolean>()
    val navigateToAddEditFragment: LiveData<Boolean>
        get() = _navigateToAddEditFragment

    private val _navigateToAddEditFragmentWithDriver = MutableLiveData<Driver>()
    val navigateToAddEditFragmentWithDriver: LiveData<Driver>
        get() = _navigateToAddEditFragmentWithDriver

    private val _doneDeletingItem = MutableLiveData<Boolean>()
    val doneDeletingItem: LiveData<Boolean>
        get() = _doneDeletingItem


    init {
        repo = DriverRepository(database)
    }

    fun onFabClicked() {
        _navigateToAddEditFragment.value = true
    }

    fun doneNavigating() {
        _navigateToAddEditFragment.value = null
    }


    fun onListItemClicked(driver: Driver) {
        _navigateToAddEditFragmentWithDriver.value = driver
    }

    fun doneNavigatingWithID() {
        _navigateToAddEditFragmentWithDriver.value = null
    }


    fun onListItemSwipeRight() {
        _doneDeletingItem.value = null
    }

    private fun doneDeletingItem() {
        _doneDeletingItem.value = true
    }

    fun onConfirmDelete(position: Int) {
        allDrivers.value?.let {
            val driverToDelete = it[position]
            deleteDriver(driverToDelete)
        }
    }

    private fun deleteDriver(driver: Driver) {
        uiScope.launch {
            repo.DeleteDriverFromDatabase(driver)
        }
        doneDeletingItem()
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}