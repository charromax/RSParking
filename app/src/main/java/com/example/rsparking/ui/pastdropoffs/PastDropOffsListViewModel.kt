package com.example.rsparking.ui.pastdropoffs

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

class PastDropOffsListViewModel(application: Application) : AndroidViewModel(application) {
    private val database: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val repo: DropOffRepository

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    val allDropoffs = database.getAllDropOffsPickedUp()
    private var undoList = ArrayList<DropOff>()

    private val _dropOffsByDate = MutableLiveData<List<DropOff>>()
    val dropOffsByDate: LiveData<List<DropOff>>
        get() = _dropOffsByDate

    private val _finishExport = MutableLiveData<Boolean>()
    val finishExport: LiveData<Boolean>
        get() = _finishExport

    private val _navigateToAddEditFragmentWithDropOff = MutableLiveData<DropOff>()
    val navigateToAddEditFragmentWithDropOff: LiveData<DropOff>
        get() = _navigateToAddEditFragmentWithDropOff

    private val _doneDeletingItem = MutableLiveData<Boolean>()
    val doneDeletingItem: LiveData<Boolean>
        get() = _doneDeletingItem

    init {
        repo = DropOffRepository(database)
        allDropoffs.value?.let {
            undoList = ArrayList(it)
        }
    }

    fun onListItemClicked(dropOff: DropOff) {
        _navigateToAddEditFragmentWithDropOff.value = dropOff
    }

    fun doneNavigatingWithID() {
        _navigateToAddEditFragmentWithDropOff.value = null
    }

    fun doneDeletingItem() {
        _doneDeletingItem.value = null
    }

    fun onConfirmDelete(position: Int) {
        //TODO delete from database all where isPickedUp == true
        allDropoffs.value?.let {
            val dropOffToDelete = it[position]
            deleteDropOff(dropOffToDelete)
        }
    }

    fun putBackIntoList() {
        uiScope.launch {
            if (undoList.size > 0) {
                for (item in undoList) {
                    repo.saveNewDropOff(item)
                }
            }
        }
    }

    fun onFinishExport() {
        _finishExport.value = true
    }

    fun onFinishDeleteAll() {
        _finishExport.value = null
        _doneDeletingItem.value = true
    }

    fun onConfirmDeleteAll() {
        deleteAll()
    }

    private fun deleteAll() {
        uiScope.launch {
            repo.deleteAllDropOffPickedUp()
        }
    }

    private fun deleteDropOff(dropOff: DropOff) {
        uiScope.launch {
            repo.DeleteDropOffFromDatabase(dropOff)
        }
        doneDeletingItem()
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }
}