package com.example.rsparking.ui.client.list

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
import com.example.rsparking.data.model.Client
import com.example.rsparking.databinding.ClientListFragmentBinding
import com.example.rsparking.ui.driver.addedit.FRAG_TITLE
import com.example.rsparking.ui.driver.list.DriverListFragmentDirections

const val FRAG_TITLE = "Client List"
class ClientListFragment: Fragment() {

    private lateinit var viewModel: ClientListViewModel
    private lateinit var binding: ClientListFragmentBinding
    private lateinit var adapter: ClientListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.client_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        activity?.actionBar?.title = FRAG_TITLE
        val viewModelFactory =
            ClientListViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ClientListViewModel::class.java)
        binding.listViewModel = viewModel
        setupAdapter()
        binding.clientList.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(listTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.clientList)

        viewModel.allClients.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    ClientListFragmentDirections.actionClientListFragmentToAddEditClientFragment(
                        null
                    )
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToAddEditFragmentWithClient.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    ClientListFragmentDirections.actionClientListFragmentToAddEditClientFragment(it)
                )
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
        adapter = ClientListAdapter(object :
            ClientListClickListener {
            override fun onClick(client: Client) {
                viewModel.onListItemClicked(client)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.clientList.adapter = null
    }

    private val listTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
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

        }

    }
}