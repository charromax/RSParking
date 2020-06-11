package com.example.rsparking.ui.driver.addedit

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
import com.example.rsparking.data.model.Driver
import com.example.rsparking.databinding.DriverAddFragmentBinding
import com.example.rsparking.util.Constants
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

class AddEditDriverFragment: Fragment() {
    private lateinit var binding: DriverAddFragmentBinding
    private lateinit var viewModelAddEdit: AddEditDriverViewModel
    private val formatter= SimpleDateFormat(Constants.FOR_SQL)


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

        viewModelAddEdit.saveDriverEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                viewModelAddEdit.saveDriver(setNewDriver())
                viewModelAddEdit.doneSaving()
                Snackbar.make(this.requireView(), R.string.saved_succesfully, Snackbar.LENGTH_SHORT).show()
                this.findNavController().navigateUp()
            }
        })

        return binding.root
    }

    private fun setNewDriver(): Driver {
        return Driver(
            0,
            binding.txtName.text.toString(),
            binding.txtLastName.text.toString(),
            binding.txtPhone.text.toString(),
            binding.txtEmail.text.toString(),
            "imagen",
            formatter.format(Date())
        )
    }

}