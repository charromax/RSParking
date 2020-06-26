package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_DROPOFFS)
data class DropOff(
    @PrimaryKey
    var id: String = "",
    var dateAdded: String = "",
    var dateOUT: String = "",
    var clientName: String = "",
    var clientPhone: String = "",
    var plateNumber: String = "",
    var parkingLot: String = "",
    var isPickedUp: Boolean = false,
    var realDateOut: String = "",
    var serviceType: String = "NONE",
    var feeType: String = "Hourly"
) : Parcelable {
}