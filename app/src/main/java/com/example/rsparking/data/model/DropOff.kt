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
    var clientID: String = "",
    var clientName: String = "",
    var clientPhone: String = "",
    var plateNumber: String = "",
    var isCrew: Boolean = false,
    var parkingLot: String = "",
    var isPickedUp: Boolean = false,
    var realDateOut: String = "",
    var serviceType: String = "NONE",
    var feeType: String = "Hourly",
    var notes: String = "",
    var score: Float = 0.0f
) : Parcelable {
    override fun toString(): String {
        return "${plateNumber},${clientName},${clientPhone},${isCrew},${dateAdded},${dateOUT},${realDateOut},${parkingLot},${serviceType},${feeType},${score}"
    }

    fun toArrayList(): ArrayList<String> {
        var array = ArrayList<String>()
        array.addAll(
            listOf(
                this.plateNumber,
                this.clientName,
                this.clientPhone,
                this.isCrew.toString(),
                this.dateAdded,
                this.dateOUT,
                this.realDateOut,
                this.parkingLot,
                this.serviceType,
                this.feeType,
                this.score.toString()
            )
        )
        return array
    }
}