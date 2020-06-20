package com.example.rsparking.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ListItem(
    val title: String = "",
    val subTitle: String = "",
    val extraInfo: String = "",
    val profilePic: String = ""
) : Parcelable {
}