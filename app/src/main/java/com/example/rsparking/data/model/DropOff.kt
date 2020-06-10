package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_DROPOFFS)
class DropOff(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
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