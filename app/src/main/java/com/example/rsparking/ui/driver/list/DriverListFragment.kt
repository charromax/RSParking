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
import com.example.rsparking.ui.driver.addedit.AddEditDriverViewModel
import com.example.rsparking.ui.driver.addedit.AddEditDriverViewModelFactory

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
        val viewModelFactory=
            DriverListViewModelFactory(
                application
            )
        viewModel= ViewModelProviders.of(this, viewModelFactory).get(DriverListViewModel::class.java)
        binding.listViewModel= viewModel
        val adapter= DriverListAdapter()
        binding.driverList.adapter= adapter

        viewModel.allDrivers.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.driverList= it
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(DriverListFragmentDirections.actionDriverListFragmentToAddEditDriverFragment())
                viewModel.doneNavigating()
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        
    }
}