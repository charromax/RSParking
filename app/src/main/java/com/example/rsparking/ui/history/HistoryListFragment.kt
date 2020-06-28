package com.example.rsparking.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.HistoryListFragmentBinding
import com.example.rsparking.ui.driver.addedit.FRAG_TITLE
import com.google.android.material.snackbar.Snackbar

const val FRAG_TITLE = "DropOff List"

class HistoryListFragment : Fragment() {

    private lateinit var viewModel: HistoryListViewModel
    private lateinit var binding: HistoryListFragmentBinding
    private lateinit var adapter: HistoryListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.history_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        activity?.actionBar?.title = FRAG_TITLE
        val viewModelFactory =
            HistoryViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(HistoryListViewModel::class.java)
        binding.listViewModel = viewModel
        binding.historyList.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        setupAdapter()
        binding.historyList.adapter = adapter

        viewModel.allDropoffs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

//        viewModel.navigateToAddEditFragmentWithDropOff.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                this.findNavController().navigate(
//                    HistoryListFragmentDirections.actionDropOffListFragmentToAddEditDropOffFragment(
//                        selectedDropOff = it
//                    )
//                )
//                viewModel.doneNavigatingWithID()
//            }
//        })
        viewModel.doneDeletingItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Snackbar.make(binding.root, R.string.on_item_deleted, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        })

        return binding.root
    }

    private fun setupAdapter() {
        adapter = HistoryListAdapter(object :
            HistoryListClickListener {
            override fun onClick(dropOff: DropOff) {
                viewModel.onListItemClicked(dropOff)
            }
        }, requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.historyList.adapter = null
    }

}