package com.example.rsparking.ui.driver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.rsparking.R
import com.example.rsparking.data.RoomDatabase.DriverDAO
import com.example.rsparking.data.RoomDatabase.RSParkingDatabase
import com.example.rsparking.databinding.DriverAddFragmentBinding

class AddEditDriverFragment: Fragment() {
    private lateinit var binding: DriverAddFragmentBinding
    private lateinit var viewModel: DriverViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.driver_add_fragment, container, false)
        binding.lifecycleOwner= this
        val application= requireNotNull(this.activity).application
        val driverDAO= RSParkingDatabase.getInstance(application).driverDAO
        val viewModelFactory= DriverViewModelFactory(driverDAO, application)
        viewModel= ViewModelProviders.of(this, viewModelFactory).get(DriverViewModel::class.java)
        binding.driverViewModel= viewModel

        return binding.root
    }

}