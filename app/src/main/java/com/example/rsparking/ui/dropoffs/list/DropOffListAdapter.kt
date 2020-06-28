package com.example.rsparking.ui.dropoffs.list

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.databinding.RecyclerItemLayoutBinding

class DropOffListAdapter(var clickListener: DropOffListClickListener, private val context: Context) :
    ListAdapter<DropOff, DropOffListAdapter.ViewHolder>(
        DropOffListDiffCallBack()
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it, clickListener, context)
        } //item is passed automatically to bind function

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(
            parent
        )
    }

    class ViewHolder private constructor(
        val binding: RecyclerItemLayoutBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
            dropOff: DropOff,
            listener: DropOffListClickListener,
            context: Context
        ) {

            val listItem = ListItem(
                id = dropOff.id,
                title = "Plate #: ${dropOff.plateNumber}\nName: ${dropOff.clientName}",
                subTitle = "Date OUT: ${dropOff.dateOUT}",
                extraInfo = "Service: ${dropOff.serviceType} - Fee: ${dropOff.feeType}",
                profilePic = ""
            )
            binding.listItem = listItem
            binding.root.setOnClickListener { listener.onClick(dropOff) }
            if(dropOff.isPickedUp == true) {
                itemView.setBackgroundColor(context.resources.getColor(android.R.color.holo_green_light))
            }
            binding.executePendingBindings()

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

class DropOffListDiffCallBack : DiffUtil.ItemCallback<DropOff>() {
    override fun areItemsTheSame(oldItem: DropOff, newItem: DropOff): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DropOff, newItem: DropOff): Boolean {
        return oldItem == newItem
    }

}

interface DropOffListClickListener {
    fun onClick(dropOff: DropOff)
}


