package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.rsparking.util.Constants
import com.example.rsparking.util.Converter
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
    @TypeConverters(Converter::class)
    var score: ArrayList<Float>?
) : Parcelable {

}