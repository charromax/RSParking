package com.example.rsparking.ui.pastdropoffs

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.PastDropoffsListFragmentBinding
import com.example.rsparking.util.ToolbarInterface
import com.google.android.material.snackbar.Snackbar


const val FRAG_TITLE = "PastDropOffs List"

class PastDropOffsListFragment : Fragment() {

    private lateinit var viewModel: PastDropOffsListViewModel
    private lateinit var binding: PastDropoffsListFragmentBinding
    private lateinit var adapter: PastDropOffsListAdapter
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
            R.layout.past_dropoffs_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory =
            PastDropOffsViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(PastDropOffsListViewModel::class.java)
        binding.listViewModel = viewModel
        toolbarCallback.getToolbarResources(FRAG_TITLE, 1)
        binding.historyList.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        setupAdapter()
        binding.historyList.adapter = adapter

        viewModel.allDropoffs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.updateList(it)
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
        adapter = PastDropOffsListAdapter(object :
            PastDropOffsListClickListener {
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