package com.example.rsparking.util

import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.rsparking.R
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.util.SpinnerExtensions.setSpinnerEntries
import com.example.rsparking.util.SpinnerExtensions.setSpinnerItemSelectedListener

@BindingAdapter("title")
fun TextView.setListItemTitle(listItem: ListItem?) {          // tengo la sensacion de que estos estan al pedo
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

@BindingAdapter("image")                            // este no
fun ImageView.setProfilePic(imageString: String?) {
    imageString?.let {
        Glide.with(context)
            .load(imageString)
            .error(R.drawable.ic_drivers)
            .into(this)
    }
}

@BindingAdapter("entries")                         //este tampoco
fun Spinner.setEntries(entries: ArrayList<String>?) {
    setSpinnerEntries(entries)
}

@BindingAdapter("newValue")
fun Spinner.setSpinnerValue(value: Any?) {
    if (adapter != null) {
        val position = (adapter as ArrayAdapter<Any>).getPosition(value)
        setSelection(position, false)
        tag = position
    }
}

@BindingAdapter("onItemSelectedListener")                         //este tampoco
fun Spinner.setOnItemSelectedListener(listener: SpinnerExtensions.ItemSelectedListener) {
    setSpinnerItemSelectedListener(listener)
}