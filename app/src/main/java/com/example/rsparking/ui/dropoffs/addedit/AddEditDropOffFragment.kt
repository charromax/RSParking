package com.example.rsparking.ui.dropoffs.addedit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.DropOffAddFragmentBinding
import com.example.rsparking.util.Constants
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CAMERA = 1
const val FRAG_TITLE = "Add/Edit DropOff"
const val PERMISSION_REQUEST_CODE = 101

class AddEditDropOffFragment : Fragment() {
    private var currentDropOff: DropOff? = null
    private lateinit var binding: DropOffAddFragmentBinding
    private lateinit var viewModel: AddEditDropOffViewModel
    val args: AddEditDropOffFragmentArgs by navArgs()
    private val formatter = SimpleDateFormat(Constants.FOR_SQL)
    private var dropOffIDarg: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.drop_off_add_fragment, container, false)
        binding.lifecycleOwner = this
        val actionbar = requireActivity().actionBar
        actionbar?.title = FRAG_TITLE
        val application = requireNotNull(this.activity).application

        args.selectedDropOff?.let {
            dropOffIDarg = it.id
            currentDropOff = it
        }
        val viewModelFactory =
            AddEditDropOffViewModelFactory(
                dropOffIDarg,
                application
            )

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(AddEditDropOffViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.saveDropOffEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                currentDropOff?.let {
                    viewModel.updateDropOff()
                    viewModel.doneUpdating()
                } ?: viewModel.saveDropOff(setNewDropOff())

                viewModel.doneSaving()
                Snackbar.make(this.requireView(), R.string.saved_succesfully, Snackbar.LENGTH_SHORT)
                    .show()
                this.findNavController().navigateUp()
            }
        })
        viewModel.currentDropOff.observe(viewLifecycleOwner, Observer {
            Log.i("FRAG_TITLE", "dropOFf name: ${it.clientName}")
        })


        return binding.root
    }


    private fun setNewDropOff(): DropOff {
        return DropOff(
            id = currentDropOff?.id ?: UUID.randomUUID().toString(),
            dateAdded = currentDropOff?.dateAdded ?: formatter.format(Date()),
            dateOUT = binding.txtDateOut.text.toString(),
            clientName = currentDropOff?.clientName ?: binding.txtClientName.text.toString(),
            plateNumber = binding.txtPlateNumber.text.toString(),
            clientPhone = binding.txtClientPhone.text.toString(),
            parkingLot = binding.txtParkingLot.text.toString(),
            serviceType = binding.txtServiceType.text.toString(),
            feeType = binding.txtFeeType.text.toString()
        )
    }

}