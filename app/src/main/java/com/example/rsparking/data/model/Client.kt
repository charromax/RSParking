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
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String= "",
    val lastName: String= "",
    val phone: String= "",
    val eMail: String= "",
    val notes: String= "",
    val dateAdded: String= "",
    @TypeConverters(Converter::class)
    val score: ArrayList<Float>): Parcelable {

}