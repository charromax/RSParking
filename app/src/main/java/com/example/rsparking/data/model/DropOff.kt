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
    var vehicleID: String = "",
    var parkingLot: String = "",
    var isPickedUp: Boolean = false,
    var realDateOut: String = "",
    var serviceType: String = ServiceType.NONE.code,
    var planCode: String = PayPlan.DAILY.code,
    var notes: String = "",
    var redCard: Boolean = false,
    var score: Float = 0.0f,
    var cost: Double = 0.0
) : Parcelable {
    override fun toString(): String {
        return "${vehicleID},${clientID},${dateAdded},${dateOUT},${realDateOut},${parkingLot}," +
                "${serviceType},${planCode},$redCard, ${score}, $cost"
    }

    fun toArrayList(): ArrayList<String> {
        var array = ArrayList<String>()
        array.addAll(
            listOf(
                this.vehicleID,
                this.clientID,
                this.dateAdded,
                this.dateOUT,
                this.realDateOut,
                this.parkingLot,
                this.serviceType,
                this.planCode,
                this.redCard.toString(),
                this.score.toString(),
                this.cost.toString()
            )
        )
        return array
    }
}