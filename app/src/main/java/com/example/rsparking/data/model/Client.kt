package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_CLIENTS)
data class Client(
    @PrimaryKey
    var id: String = "",
    var name: String = "",
    var phone: String = "",
    var eMail: String = "",
    var plateNumber: String = "",
    var notes: String = "",
    var dateAdded: String = "",
    var isCrew: Boolean = false,
    var score: Float = 0.0f
) : Parcelable {
    override fun toString(): String {
        return "${plateNumber}"
    }

    fun toArrayList(): ArrayList<String> {
        var array = ArrayList<String>()
        array.addAll(
            listOf(
                this.id,
                this.name,
                this.phone,
                this.eMail,
                this.plateNumber,
                this.dateAdded,
                this.isCrew.toString(),
                this.score.toString()
            )
        )
        return array
    }

}