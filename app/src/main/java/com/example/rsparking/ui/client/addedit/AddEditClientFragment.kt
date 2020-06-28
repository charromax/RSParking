package com.example.rsparking.ui.client.addedit

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
import com.example.rsparking.data.model.Client
import com.example.rsparking.databinding.ClientAddEditFragmentBinding
import com.example.rsparking.util.Constants
import com.google.android.material.snackbar.Snackbar
import java.text.SimpleDateFormat
import java.util.*

const val FRAG_TITLE = "Add/Edit Fragment"

class AddEditClientFragment : Fragment() {
    private var currentClient: Client? = null
    private lateinit var viewModelAddEdit: AddEditClientViewModel
    private lateinit var binding:ClientAddEditFragmentBinding
    val args: AddEditClientFragmentArgs by navArgs()
    private val formatter = SimpleDateFormat(Constants.FOR_SQL)
    private var clientIDarg: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.client_add_edit_fragment, container, false)
        binding.lifecycleOwner = this
        val actionbar = requireActivity().actionBar
        actionbar?.title = FRAG_TITLE
        val application = requireNotNull(this.activity).application

        args.selectedClient?.let {
            clientIDarg = it.id
            currentClient = it
        }
        val viewModelFactory =
            AddEditClientViewModelFactory(
                clientIDarg,
                application
            )

        viewModelAddEdit = ViewModelProviders.of(this, viewModelFactory)
            .get(AddEditClientViewModel::class.java)
        binding.viewModel = viewModelAddEdit

        viewModelAddEdit.saveClientEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                currentClient?.let {
                    viewModelAddEdit.updateClient()
                    viewModelAddEdit.doneUpdating()
                } ?: viewModelAddEdit.saveClient(setNewClient())

                viewModelAddEdit.doneSaving()
                Snackbar.make(this.requireView(), R.string.saved_succesfully, Snackbar.LENGTH_SHORT)
                    .show()
                this.findNavController().navigateUp()
            }
        })
        viewModelAddEdit.currentClient.observe(viewLifecycleOwner, Observer {
            Log.i("FRAG_TITLE", "client name: ${it.name}")
        })


        return binding.root
    }

    private fun setNewClient(): Client {
        return Client(
            id = currentClient?.id ?: UUID.randomUUID().toString(),
            name = binding.txtName.text.toString(),
            eMail = binding.txtEmail.text.toString(),
            dateAdded = currentClient?.dateAdded ?: formatter.format(Date()),
            score = arrayListOf()
        )
    }

}