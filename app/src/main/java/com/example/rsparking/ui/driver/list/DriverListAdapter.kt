package com.example.rsparking.ui.driver.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.rsparking.R
import com.example.rsparking.data.model.Driver
import kotlinx.android.synthetic.main.recycler_item_layout.view.*

class DriverListAdapter: RecyclerView.Adapter<DriverListAdapter.ViewHolder>() {
    var driverList= listOf<Driver>()
        set(value){
            field = value
            notifyDataSetChanged()
        }
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(driver: Driver) {
            itemView.mainTitle.text= "${driver.name} ${driver.lastName}"
            itemView.subTitle.text= "Tel: ${driver.phone}"
            itemView.extraInfo.text= "Email: ${driver.eMail}"
            Glide.with(itemView)
                .load(driver.image)
                .into(itemView.profileImage)
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = driverList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val driver= driverList.get(position)
        holder.bind(driver)
    }
}
