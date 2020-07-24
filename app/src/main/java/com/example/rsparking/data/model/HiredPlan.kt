package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_HIRED_PLAN)
data class HiredPlan(
    @PrimaryKey
    val id: String,
    val clientID: String,
    val planCode: String,
    val initDate: String,
    val endDate: String,
    val absentDate1: String,         //vacations init date
    val absentDate2: String,         // vacations end date
    val remainingCarWash: Int
) : Parcelable {

}