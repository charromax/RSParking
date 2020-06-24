package com.example.rsparking.ui.dropoffs.addedit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddEditDropOffViewModelFactory(
    private val dropOffID: String?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditDropOffViewModel::class.java)) {
            return AddEditDropOffViewModel(
                dropOffID,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}