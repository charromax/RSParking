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
    var notes: String = "",
    var dateAdded: String = "",
    var isCrew: Boolean = false,
    var score: Float = 0.0f
) : Parcelable {
    override fun toString(): String {
        return "${name}"
    }

    fun toArrayList(): ArrayList<String> {   //convert to arraylist of strings to be used
        var array = ArrayList<String>()      // for exporting csv tables
        array.addAll(
            listOf(
                this.id,
                this.name,
                this.phone,
                this.eMail,
                this.dateAdded,
                this.isCrew.toString(),
                this.score.toString()
            )
        )
        return array
    }

}