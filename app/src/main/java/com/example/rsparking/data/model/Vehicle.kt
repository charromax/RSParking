package com.example.rsparking.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Vehicle(
    val plateNumber: String= "",
    val idClient: String= "",
    val brand: String= "",
    val model: String= "",
    val color: String= "",
    val parkingLot: String= "",
    val image: String= "",
    val notes: String= "",
    val dateAdded: String= ""): Parcelable {
}