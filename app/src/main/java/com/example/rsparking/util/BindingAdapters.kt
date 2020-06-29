package com.example.rsparking.util

import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.example.rsparking.R
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.util.SpinnerExtensions.getSpinnerValue
import com.example.rsparking.util.SpinnerExtensions.setSpinnerEntries
import com.example.rsparking.util.SpinnerExtensions.setSpinnerInverseBindingListener
import com.example.rsparking.util.SpinnerExtensions.setSpinnerValue


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

//@BindingAdapter("rating")
//fun RatingBar.setRating(score: Float?) {
//    score?.let {
//        rating = score
//    }
//}

@BindingAdapter("entries")                         //este tampoco
fun Spinner.setEntries(entries: ArrayList<String>?) {
    setSpinnerEntries(entries)
}

@BindingAdapter("selectedValue")
fun Spinner.setSelectedValue(selectedValue: String?) {
    setSpinnerValue(selectedValue)
}

@BindingAdapter("selectedValueAttrChanged")
fun Spinner.setInverseBindingListener(inverseBindingListener: InverseBindingListener?) {
    setSpinnerInverseBindingListener(inverseBindingListener)
}

object InverseSpinnerBindings {

    @JvmStatic
    @InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
    fun Spinner.getSelectedValue(): Any? {
        return getSpinnerValue()
    }
}