package com.example.rsparking.ui.client.addedit

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class AddEditClientViewModelFactory(
    private val clientID: String?,
    private val application: Application
) : ViewModelProvider.Factory {
    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddEditClientViewModel::class.java)) {
            return AddEditClientViewModel(
                clientID,
                application
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}