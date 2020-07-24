package com.example.rsparking.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rsparking.util.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = Constants.TABLE_SCORE)
data class Score(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val clientID: String,
    val score: Double
) : Parcelable {
}