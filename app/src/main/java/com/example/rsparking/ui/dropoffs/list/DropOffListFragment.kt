package com.example.rsparking.ui.dropoffs.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.DropOffListFragmentBinding
import com.example.rsparking.ui.driver.addedit.FRAG_TITLE

const val FRAG_TITLE = "DropOff List"

class DropOffListFragment : Fragment() {

    private lateinit var viewModel: DropOffListViewModel
    private lateinit var binding: DropOffListFragmentBinding
    private lateinit var adapter: DropOffListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.drop_off_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        activity?.actionBar?.title = FRAG_TITLE
        val viewModelFactory =
            DropOffViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DropOffListViewModel::class.java)
        binding.listViewModel = viewModel
        setupAdapter()
        binding.dropOffList.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(listTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.dropOffList)

        viewModel.allDropoffs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DropOffListFragmentDirections.actionDropOffListFragmentToAddEditDropOffFragment(
                        selectedDropOff = null
                    )
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToAddEditFragmentWithDropOff.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DropOffListFragmentDirections.actionDropOffListFragmentToAddEditDropOffFragment(
                        selectedDropOff = it
                    )
                )
                //TODO navigate to details
                viewModel.doneNavigatingWithID()
            }
        })
        viewModel.doneDeletingItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Toast.makeText(requireContext(), R.string.on_item_deleted, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })

        return binding.root
    }

    private fun setupAdapter() {
        adapter = DropOffListAdapter(object :
            DropOffListClickListener {
            override fun onClick(dropOff: DropOff) {
                viewModel.onListItemClicked(dropOff)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.dropOffList.adapter = null
    }

    private val listTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition

            if (direction == ItemTouchHelper.RIGHT) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm delete")
                builder.setMessage(resources.getString(R.string.confirm_delete))
                builder.setPositiveButton(R.string.yes) { dialog, which ->
                    viewModel.onListItemSwipeRight()
                    viewModel.onConfirmDelete(position)
                }
                builder.setNegativeButton(R.string.no) { dialog, which ->
                    adapter.notifyItemChanged(position)
                    Toast.makeText(requireContext(), R.string.on_cancel_delete, Toast.LENGTH_SHORT)
                        .show()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                //TODO swipe left to deliver car
            }

        }

    }
}