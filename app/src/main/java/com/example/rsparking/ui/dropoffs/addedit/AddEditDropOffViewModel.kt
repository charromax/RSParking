package com.example.rsparking.ui.dropoffs.addedit

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


class AddEditDropOffViewModel(
    private val dropOffID: String?, application: Application
) : AndroidViewModel(application) {
    private val database: DropOffDAO = RSParkingDatabase.getInstance(application).dropOffDAO
    private val repo: DropOffRepository
    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)


    // drop off info
    val currentDropOff = MutableLiveData<DropOff>()


    private val _saveDropOffEvent = MutableLiveData<Boolean>()
    val saveDropOffEvent: LiveData<Boolean>
        get() = _saveDropOffEvent

    private val _updateDropOffEvent = MutableLiveData<Boolean>()
    val updateDropOffEvent: LiveData<Boolean>
        get() = _updateDropOffEvent

    init {
        repo = DropOffRepository(database)
        dropOffID?.let {
            getDropOff(it)
        }
    }

    fun saveDropOff(dropOff: DropOff) {
        uiScope.launch {
            repo.saveNewDropOff(dropOff)
        }
    }

    fun updateDropOff() {
        uiScope.launch {
            currentDropOff.value?.let { repo.UpdateDropOff(it) }
        }
    }

    fun doneSaving() {
        _saveDropOffEvent.value = null
    }

    fun onSaveEvent() {
        _saveDropOffEvent.value = true
    }

    fun getDropOff(key: String) {
        uiScope.launch {
            currentDropOff.value = repo.getDropOffByID(key)
        }
    }

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

    fun onUpdateEvent() {
        _updateDropOffEvent.value = true
    }

    fun doneUpdating() {
        _updateDropOffEvent.value = null
    }


}