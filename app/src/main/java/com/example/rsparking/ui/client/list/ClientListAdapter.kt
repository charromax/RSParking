package com.example.rsparking.ui.client.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.data.model.Client
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.databinding.RecyclerItemLayoutBinding

class ClientListAdapter(var clickListener: ClientListClickListener) :
    ListAdapter<Client, ClientListAdapter.ViewHolder>(
        ClientListDiffCallback()
    ) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let {
            holder.bind(it, clickListener)
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
            client: Client,
            listener: ClientListClickListener
        ) {

            val listItem = ListItem(
                id = client.id,
                title = "${client.name} - Plate #: ${client.plateNumber}",
                subTitle = "Tel: ${client.phone}",
                extraInfo = "Client since: ${client.dateAdded}",
                profilePic = ""
            )
            binding.listItem = listItem
            binding.executePendingBindings()
            binding.root.setOnClickListener { listener.onClick(client) }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = RecyclerItemLayoutBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(
                    binding
                )
            }
        }

    }
}

class ClientListDiffCallback : DiffUtil.ItemCallback<Client>() {
    override fun areItemsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Client, newItem: Client): Boolean {
        return oldItem == newItem
    }

}

interface ClientListClickListener {
    fun onClick(client: Client)
}


