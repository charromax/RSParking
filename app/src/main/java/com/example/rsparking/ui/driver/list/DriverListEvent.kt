package com.example.rsparking.ui.driver.list

sealed class DriverListEvent {
    data class onListItemClick(val position: Int) : DriverListEvent()
    object onNewDriverClick : DriverListEvent()
}
