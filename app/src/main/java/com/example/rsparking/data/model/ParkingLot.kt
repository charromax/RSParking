package com.example.rsparking.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ParkingLot(
    val id: String,
    val isFree: Boolean= true
): Parcelable {
}