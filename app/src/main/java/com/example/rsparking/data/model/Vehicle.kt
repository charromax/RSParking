package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_VEHICLES)
data class Vehicle(
    @PrimaryKey
    val plateNumber: String= "",

    val idClient: Int=0,
    val brand: String= "",
    val model: String= "",
    val color: String= "",
    val parkingLot: String= "",
    val image: String= "",
    val notes: String= "",
    val dateAdded: String= ""): Parcelable {
}