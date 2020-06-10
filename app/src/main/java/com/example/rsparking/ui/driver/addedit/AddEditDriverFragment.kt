package com.example.rsparking.ui.driver.addedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.rsparking.R
import com.example.rsparking.databinding.DriverAddFragmentBinding

class AddEditDriverFragment: Fragment() {
    private lateinit var binding: DriverAddFragmentBinding
    private lateinit var viewModelAddEdit: AddEditDriverViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= DataBindingUtil.inflate(inflater, R.layout.driver_add_fragment, container, false)
        binding.lifecycleOwner= this
        val application= requireNotNull(this.activity).application
        val viewModelFactory=
            AddEditDriverViewModelFactory(
                application
            )
        viewModelAddEdit= ViewModelProviders.of(this, viewModelFactory).get(AddEditDriverViewModel::class.java)
        binding.driverViewModel= viewModelAddEdit

        return binding.root
    }

}