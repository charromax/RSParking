package com.example.rsparking.ui.driver.list

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.data.model.Driver
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.databinding.RecyclerItemLayoutBinding
import com.example.rsparking.ui.driver.list.DriverListEvent.onListItemClick

class DriverListAdapter(
    private val viewModel: DriverListViewModel,
    private val activity: Activity,
    val event: MutableLiveData<DriverListEvent> = MutableLiveData()
) : ListAdapter<Driver, DriverListAdapter.ViewHolder>(DriverListDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let(holder::bind)  //item is passed automatically to bind function
        //TODO onclicklistener
        holder.binding.root.setOnClickListener {
            event.value = onListItemClick(position)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent, activity)
    }

    class ViewHolder private constructor(
        val binding: RecyclerItemLayoutBinding,
        val activity: Activity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(driver: Driver) {
            val listItem = ListItem(
                title = "${driver.name} ${driver.lastName}",
                subTitle = "Tel: ${driver.phone}",
                extraInfo = "Email: ${driver.eMail}",
                profilePic = driver.image
            )
            binding.listItem = listItem
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup, activity: Activity): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerItemLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding, activity)
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
