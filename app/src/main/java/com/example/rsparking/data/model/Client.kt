package com.example.rsparking.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Client(
    val id: String="",
    val name: String= "",
    val lastName: String= "",
    val phone: String= "",
    val eMail: String= "",
    val score: ArrayList<Float>?= null,
    val notes: String= "",
    val dateAdded: String= ""): Parcelable {

}