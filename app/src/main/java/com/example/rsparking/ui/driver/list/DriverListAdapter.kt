package com.example.rsparking.ui.driver.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.databinding.RecyclerItemLayoutBinding

class DriverListAdapter(var clickListener: DriverListClickListener) :
    ListAdapter<Driver, DriverListAdapter.ViewHolder>(DriverListDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it, clickListener)
        } //item is passed automatically to bind function

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(
        val binding: RecyclerItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            driver: Driver,
            listener: DriverListClickListener
        ) {

            val listItem = ListItem(
                id = driver.id,
                title = "${driver.name} ${driver.lastName}",
                subTitle = "Tel: ${driver.phone}",
                extraInfo = "Email: ${driver.eMail}",
                profilePic = driver.image
            )
            binding.listItem = listItem
            binding.executePendingBindings()
            binding.root.setOnClickListener { listener.onClick(driver) }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerItemLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }

    }
}

class DriverListDiffCallback : DiffUtil.ItemCallback<Driver>() {
    override fun areItemsTheSame(oldItem: Driver, newItem: Driver): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Driver, newItem: Driver): Boolean {
        return oldItem == newItem
    }

}

interface DriverListClickListener {
    fun onClick(driver: Driver)
}


