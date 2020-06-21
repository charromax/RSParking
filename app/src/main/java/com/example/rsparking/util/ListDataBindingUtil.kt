package com.example.rsparking.util

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.rsparking.R
import com.example.rsparking.data.model.ListItem

@BindingAdapter("title")
fun TextView.setListItemTitle(listItem: ListItem?) {
    listItem?.let {
        text = it.title
    }
}

@BindingAdapter("subTitle")
fun TextView.setListItemSubtitle(listItem: ListItem?) {
    listItem?.let {
        text = it.subTitle
    }
}

@BindingAdapter("extraInfo")
fun TextView.setListItemExtra(listItem: ListItem?) {
    listItem?.let {
        text = it.extraInfo
    }
}

@BindingAdapter("image")
fun ImageView.setProfilePic(imageString: String?) {
    imageString?.let {
        Glide.with(context)
            .load(imageString)
            .error(R.drawable.ic_drivers)
            .into(this)
    }
}