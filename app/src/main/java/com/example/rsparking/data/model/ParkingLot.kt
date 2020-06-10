package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_PARKLOTS)
data class ParkingLot(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val isFree: Boolean= true
): Parcelable {
}