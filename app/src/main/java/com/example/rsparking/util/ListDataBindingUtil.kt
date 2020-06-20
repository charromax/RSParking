package com.example.rsparking.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.rsparking.R
import com.example.rsparking.data.model.ListItem

@BindingAdapter("title")
fun TextView.setListItemTitle(listItem: ListItem) {
    text = listItem.title
}

@BindingAdapter("subTitle")
fun TextView.setListItemSubtitle(listItem: ListItem) {
    text = listItem.subTitle
}

@BindingAdapter("extraInfo")
fun TextView.setListItemExtra(listItem: ListItem) {
    text = listItem.extraInfo
}

@BindingAdapter("image")
fun ImageView.setProfilePic(listItem: ListItem) {
    Glide.with(context)
        .load(listItem.profilePic)
        .error(R.drawable.ic_drivers)
        .into(this)
}