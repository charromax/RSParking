package com.example.rsparking.ui.pastdropoffs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.data.model.ListItem
import com.example.rsparking.databinding.RecyclerItemLayoutBinding

class PastDropOffsListAdapter(
    var clickListener: PastDropOffsListClickListener,
    private val context: Context
) :
    ListAdapter<DropOff, PastDropOffsListAdapter.ViewHolder>(
        PastDropOffsListDiffCallBack()
    ), Filterable {
    private var filterList = ArrayList<DropOff>()

    fun updateList(list: MutableList<DropOff>) {
        filterList.addAll(list)
        submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(p0: CharSequence?, filterResults: FilterResults?) {
                submitList(filterResults?.values as ArrayList<DropOff>)
            }

            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val results = FilterResults()
                val suggestions = ArrayList<DropOff>()

                if (charSequence == null || charSequence.length == 0) {
                    suggestions.addAll(filterList)
                } else {
                    for (dropOff in currentList) {
                        if (dropOff.plateNumber.contains(charSequence) ||
                            dropOff.clientName.toLowerCase().contains(charSequence) ||
                            dropOff.clientPhone.toLowerCase().contains(charSequence)
                        ) {
                            suggestions.add(dropOff)
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
            listener: PastDropOffsListClickListener,
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
            if (dropOff.isPickedUp == true) {
                binding.cardView.setCardBackgroundColor(context.resources.getColor(android.R.color.holo_green_dark))
            } else {
                binding.cardView.setCardBackgroundColor(context.resources.getColor(R.color.rs_white))
            }
            if (dropOff.isCrew == true) {
                binding.crewIcon.visibility = View.VISIBLE
            } else {
                binding.crewIcon.visibility = View.GONE
            }
            binding.extraInfo.setTextColor(context.resources.getColor(R.color.rs_white))
            binding.profileImage.visibility = View.GONE
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

class PastDropOffsListDiffCallBack : DiffUtil.ItemCallback<DropOff>() {
    override fun areItemsTheSame(oldItem: DropOff, newItem: DropOff): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: DropOff, newItem: DropOff): Boolean {
        return oldItem == newItem
    }

}

interface PastDropOffsListClickListener {
    fun onClick(dropOff: DropOff)
}


