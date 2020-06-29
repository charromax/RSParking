package com.example.rsparking.ui.dropoffs.list

import android.graphics.Canvas
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.DropOffListFragmentBinding
import com.example.rsparking.util.ToolbarInterface
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


const val FRAG_TITLE = "DropOffs List"

class DropOffListFragment : Fragment() {

    private lateinit var viewModel: DropOffListViewModel
    private lateinit var binding: DropOffListFragmentBinding
    private lateinit var adapter: DropOffListAdapter
    private lateinit var toolbarCallback: ToolbarInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        toolbarCallback = activity as ToolbarInterface
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView =
            searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                adapter.filter.filter(s.toLowerCase())
                return false
            }
        })
    }

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
        toolbarCallback.getToolbarResources(FRAG_TITLE, 1)
        val viewModelFactory =
            DropOffViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DropOffListViewModel::class.java)
        binding.listViewModel = viewModel
        binding.dropOffList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        setupAdapter()
        binding.dropOffList.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(listTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.dropOffList)

        viewModel.allDropoffs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.updateList(it)
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
                viewModel.doneNavigatingWithID()
            }
        })
        viewModel.doneDeletingItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Snackbar.make(binding.root, R.string.on_item_deleted, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        })
        viewModel.doneDeliveringCar.observe(viewLifecycleOwner, Observer {
            it?.let {
                Snackbar.make(binding.root, R.string.saved_succesfully, Snackbar.LENGTH_SHORT)
                    .show()
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
        }, requireContext())
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

            if (direction == ItemTouchHelper.LEFT) {
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
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm deliver?")
                builder.setMessage(resources.getString(R.string.confirm_delivery))
                builder.setPositiveButton(R.string.yes) { dialog, which ->
                    viewModel.onListItemSwipeLeft()
                    viewModel.onConfirmDeliver(position)
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
                .addSwipeLeftActionIcon(R.drawable.ic_delete_red)
                .addSwipeRightActionIcon(R.drawable.ic_check_green)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }
}