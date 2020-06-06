package com.example.rsparking.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class DropOff(
    val id: String="",
    val dateIN: String= "",
    val dateOUT: String= "",
    val idClient: String= "",
    val plateNumber: String= "",
    val idParkingLot: String= "",
    val isPickedUp: Boolean= false,
    val realDateOut: String= "",
    val dateAdded: String= ""
): Parcelable {
}