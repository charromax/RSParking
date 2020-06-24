package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_DRIVERS)
data class Driver(
    @PrimaryKey()
    var id: String,
    var name: String,
    var lastName: String,
    var phone: String,
    var eMail: String,
    var image: String,
    val dateAdded: String
): Parcelable
{
}