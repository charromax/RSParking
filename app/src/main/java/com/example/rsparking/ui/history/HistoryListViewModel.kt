package com.example.rsparking.ui.history

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.DropOffDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.data.repo.DropOffRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class HistoryListViewModel(application: Application) : AndroidViewModel(application) {
    private val database: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val repo: DropOffRepository

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allDropoffs = database.getAllDropOffsPickedUp()

    private val _dropOffsByDate = MutableLiveData<List<DropOff>>()
    val dropOffsByDate: LiveData<List<DropOff>>
        get() = _dropOffsByDate

    private val _navigateToAddEditFragmentWithDropOff = MutableLiveData<DropOff>()
    val navigateToAddEditFragmentWithDropOff: LiveData<DropOff>
        get() = _navigateToAddEditFragmentWithDropOff

    private val _doneDeletingItem = MutableLiveData<Boolean>()
    val doneDeletingItem: LiveData<Boolean>
        get() = _doneDeletingItem

    private val _doneDeliveringCar = MutableLiveData<Boolean>()
    val doneDeliveringCar: LiveData<Boolean>
        get() = _doneDeliveringCar


    init {
        repo = DropOffRepository(database)
    }

    fun onFabClicked() {
        _navigateToAddEditFragment.value = true
    }

    fun doneNavigating() {
        _navigateToAddEditFragment.value = null
    }


    fun onListItemClicked(dropOff: DropOff) {
        _navigateToAddEditFragmentWithDropOff.value = dropOff
    }

    fun doneNavigatingWithID() {
        _navigateToAddEditFragmentWithDropOff.value = null
    }

    fun onListItemSwipeRight() {
        _doneDeletingItem.value = null
    }

    private fun doneDeletingItem() {
        _doneDeletingItem.value = true
    }

    fun onListItemSwipeLeft() {
        _doneDeliveringCar.value = null
    }

    private fun doneDeliveringCar() {
        _doneDeliveringCar.value = true
    }

    fun onConfirmDelete(position: Int) {
        allDropoffs.value?.let {
            val dropOffToDelete = it[position]
            deleteDropOff(dropOffToDelete)
        }
    }

    private fun deleteDropOff(dropOff: DropOff) {
        uiScope.launch {
            repo.DeleteDropOffFromDatabase(dropOff)
        }
        doneDeletingItem()
    }

    fun onConfirmDeliver(position: Int) {
        allDropoffs.value?.let {
            val dropOffToUpdate = it[position].copy(isPickedUp = true)
            uiScope.launch {
                repo.UpdateDropOff(dropOffToUpdate)
            }
        }
        doneDeliveringCar()
    }


    fun getDropOffByDate(date: String) {
        uiScope.launch {
            _dropOffsByDate.value = repo.getDropOffByDateOut(date)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}