package com.example.rsparking.ui.client.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.data.model.Client
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.databinding.RecyclerItemLayoutBinding

class ClientListAdapter(var clickListener: ClientListClickListener) :
    ListAdapter<Client, ClientListAdapter.ViewHolder>(
        ClientListDiffCallback()
    ), Filterable {

    private var filterList = ArrayList<Client>()

    fun updateList(list: MutableList<Client>) {
        filterList.addAll(list)
        submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                submitList(filterResults?.values as ArrayList<Client>)
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions = ArrayList<Client>()

                if (charSequence == null || charSequence.length == 0) {
                    suggestions.addAll(filterList)
                } else {
                    for (client in currentList) {
                        if (client.plateNumber.contains(charSequence) ||
                            client.name.toLowerCase().contains(charSequence) ||
                            client.phone.toLowerCase().contains(charSequence)
                        ) {
                            suggestions.add(client)
                        }
                    }
                }
                results.values = suggestions
                results.count = suggestions.size
                return results
            }
        }
    }

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
            if (client.isCrew == true) {
                binding.crewIcon.visibility = View.VISIBLE
            } else {
                binding.crewIcon.visibility = View.GONE
            }
            binding.profileImage.visibility = View.GONE
            binding.miniScore.apply {
                rating = client.score
                visibility = View.VISIBLE
            }
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


