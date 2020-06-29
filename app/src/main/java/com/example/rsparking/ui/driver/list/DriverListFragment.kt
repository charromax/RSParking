package com.example.rsparking.ui.driver.list

import android.graphics.Canvas
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
import com.example.rsparking.data.model.Driver
import com.example.rsparking.databinding.DriverListFragmentBinding
import com.example.rsparking.util.ToolbarInterface
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

const val FRAG_TITLE = "Driver List"
class DriverListFragment: Fragment() {

    private lateinit var viewModel: DriverListViewModel
    private lateinit var binding: DriverListFragmentBinding
    private lateinit var adapter: DriverListAdapter
    private lateinit var toolbarCallback: ToolbarInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        toolbarCallback = activity as ToolbarInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.driver_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        toolbarCallback.getToolbarResources(com.example.rsparking.ui.driver.list.FRAG_TITLE, 1)
        val viewModelFactory =
            DriverListViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DriverListViewModel::class.java)
        binding.listViewModel = viewModel
        setupAdapter()
        binding.driverList.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(listTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.driverList)

        viewModel.allDrivers.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DriverListFragmentDirections.actionDriverListFragmentToAddEditDriverFragment(
                        null
                    )
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToAddEditFragmentWithDriver.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DriverListFragmentDirections.actionDriverListFragmentToAddEditDriverFragment(it)
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
        adapter = DriverListAdapter(object : DriverListClickListener {
            override fun onClick(driver: Driver) {
                viewModel.onListItemClicked(driver)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.driverList.adapter = null
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

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addSwipeRightActionIcon(R.drawable.ic_delete_red)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }
}