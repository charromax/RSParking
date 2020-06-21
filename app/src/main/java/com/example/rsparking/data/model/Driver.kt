package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = Constants.TABLE_DRIVERS)
data class Driver(
    @PrimaryKey()
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val lastName: String = "",
    val phone: String = "",
    val eMail: String = "",
    val image: String = "",
    val dateAdded: String = ""
): Parcelable
{
}