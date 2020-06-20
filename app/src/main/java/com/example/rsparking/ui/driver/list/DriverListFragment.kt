package com.example.rsparking.ui.driver.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.rsparking.R
import com.example.rsparking.databinding.DriverListFragmentBinding
import com.example.rsparking.ui.driver.addedit.FRAG_TITLE

const val FRAG_TITLE = "Driver List"
class DriverListFragment: Fragment() {

    private lateinit var viewModel: DriverListViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: DriverListFragmentBinding= DataBindingUtil.inflate(
            inflater,
            R.layout.driver_list_fragment,
            container,
            false)
        binding.lifecycleOwner= this
        val application= requireNotNull(this.activity).application
        activity?.actionBar?.title = FRAG_TITLE
        val viewModelFactory=
            DriverListViewModelFactory(
                application
            )
        viewModel= ViewModelProviders.of(this, viewModelFactory).get(DriverListViewModel::class.java)
        binding.listViewModel= viewModel
        val adapter = DriverListAdapter(viewModel, requireActivity())
        binding.driverList.adapter= adapter

        viewModel.allDrivers.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DriverListFragmentDirections.actionDriverListFragmentToAddEditDriverFragment(
                        driverID = null
                    )
                )
                viewModel.doneNavigating()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }
}