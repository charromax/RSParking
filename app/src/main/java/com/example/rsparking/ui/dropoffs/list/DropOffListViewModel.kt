package com.example.rsparking.ui.dropoffs.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.rsparking.data.RoomDatabase.DropOffDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.data.repo.DropOffRepository
import com.example.rsparking.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DropOffListViewModel(application: Application) : AndroidViewModel(application) {
    private val database: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val repo: DropOffRepository
    private val formatter = SimpleDateFormat(Constants.FOR_SQL)

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allDropoffs = database.getAllDropOffsExceptPickedUp()

    private val _dropOffsByDate = MutableLiveData<List<DropOff>>()
    val dropOffsByDate: LiveData<List<DropOff>>
        get() = _dropOffsByDate

    private val _navigateToAddEditFragment = MutableLiveData<Boolean>()
    val navigateToAddEditFragment: LiveData<Boolean>
        get() = _navigateToAddEditFragment

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
        val realDateOUT = formatter.format(Date())
        allDropoffs.value?.let {
            val dropOffToUpdate = it[position].copy(
                isPickedUp = true,
                realDateOut = realDateOUT
            )
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