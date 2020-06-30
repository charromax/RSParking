package com.example.rsparking.ui.dropoffs.addedit

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.rsparking.R
import com.example.rsparking.data.model.Client
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.DropOffAddFragmentBinding
import com.example.rsparking.ui.mainactivity.TAG
import com.example.rsparking.util.Constants
import com.example.rsparking.util.ToolbarInterface
import com.example.rsparking.util.formatPhoneNumber
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import java.text.SimpleDateFormat
import java.util.*


const val FRAG_TITLE = "Add/Edit DropOff"
const val CONTACT_REQUEST_CODE = 420

class AddEditDropOffFragment : Fragment(), DatePickerDialog.OnDateSetListener,
    TimePickerDialog.OnTimeSetListener, AdapterView.OnItemClickListener {
    private var currentDropOff: DropOff? = null
    private lateinit var binding: DropOffAddFragmentBinding
    private lateinit var viewModel: AddEditDropOffViewModel
    val args: AddEditDropOffFragmentArgs by navArgs()
    private val formatter = SimpleDateFormat(Constants.FOR_SQL)
    private val formatterForMSG = SimpleDateFormat(Constants.FOR_HUMANS)
    private var dropOffIDarg: String? = null
    private var stopLoop: Boolean = false
    private var foundClient: Client? = null
    private var selectedDateOut: String = ""
    private lateinit var toolbarCallback: ToolbarInterface


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarCallback = activity as ToolbarInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.drop_off_add_fragment, container, false)
        binding.lifecycleOwner = this.viewLifecycleOwner
        val application = requireNotNull(this.activity).application

        args.selectedDropOff?.let {
            dropOffIDarg = it.id
            currentDropOff = it
            binding.isCrewCheckBox.isChecked = it.isCrew
            binding.saveClientCheckBox.visibility = View.GONE
            binding.crewIcon.visibility = if (it.isCrew) View.VISIBLE else View.GONE
        }
        val viewModelFactory =
            AddEditDropOffViewModelFactory(
                dropOffIDarg,
                application
            )

        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(AddEditDropOffViewModel::class.java)
        binding.viewModel = viewModel
        toolbarCallback.getToolbarResources(FRAG_TITLE, 1)

        viewModel.dateOutClickedEvent.observe(viewLifecycleOwner, Observer {
            val oneTime: Boolean? = null
            if (viewModel.dateOutClickedEvent.value != oneTime) {
                openDatePicker()
                viewModel.doneWithPicker()
            }
        })
        viewModel.allClients.observe(viewLifecycleOwner, Observer {
            binding.txtPlateNumber.setAdapter(
                ArrayAdapter(
                    requireContext(),
                    R.layout.support_simple_spinner_dropdown_item,
                    it
                )
            )
            binding.txtPlateNumber.setOnItemClickListener(this)
        })

        viewModel.saveDropOffEvent.observe(viewLifecycleOwner, Observer { saveORupdate ->
            if (!stopLoop) {
                stopLoop = true
                saveORupdate?.let {
                    currentDropOff?.let {
                        viewModel.updateDropOff()
                        viewModel.doneUpdating()
                        createConfirmationDialog(it)
                    }
                    if (currentDropOff == null) {
                        currentDropOff = setNewDropOff()
                        viewModel.saveDropOff(currentDropOff!!, setNewClient(currentDropOff!!))
                        viewModel.doneSaving()
                        createConfirmationDialog(currentDropOff!!)
                    }
                    //TODO aca se colgaba el loop, quedo medio una chanchada pero anda
                }
                Toast.makeText(requireContext(), R.string.saved_succesfully, Toast.LENGTH_SHORT)
                    .show()
            }

        })
        viewModel.currentDropOff.observe(viewLifecycleOwner, Observer {
            Log.i("FRAG_TITLE", "dropOff name: ${it.clientName}")
        })

        return binding.root
    }

    private fun createConfirmationDialog(dropOff: DropOff) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Send Message?")
        builder.setMessage(resources.getString(R.string.confirm_send_message))
        builder.setPositiveButton(R.string.yes) { dialog, which ->
            if (foundClient != null) {
                addContactToDevice(dropOff)
            } else {
                sendWhatsAppMsg(dropOff)
            }
        }
        builder.setNegativeButton(R.string.no) { dialog, which ->
            Toast.makeText(requireContext(), R.string.on_cancel_delete, Toast.LENGTH_SHORT)
                .show()
            this.findNavController().navigateUp()
        }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun addContactToDevice(dropOff: DropOff) {
        val formattedNumber = formatPhoneNumber(dropOff.clientPhone)
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE)
        if (Integer.valueOf(Build.VERSION.SDK) > 14)
            intent.putExtra("finishActivityOnSaveCompleted", true)
        intent.putExtra(ContactsContract.Intents.Insert.NAME, dropOff.clientName)
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, formattedNumber)
        startActivityForResult(intent, CONTACT_REQUEST_CODE)

    }
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        intent: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == CONTACT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                currentDropOff?.let {
                    sendWhatsAppMsg(it)
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(requireContext(), R.string.on_cancel_delete, Toast.LENGTH_SHORT).show()
            }
        }
        this.findNavController().navigateUp()
    }

    private fun sendWhatsAppMsg(dropOff: DropOff) {
        val pickupDate = formatter.parse(dropOff.dateOUT)
        val whatsAppAppId = "com.whatsapp"
        val packageManager = requireActivity().packageManager
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val text = String.format(
            resources.getString(
                R.string.sms_message,
                dropOff.clientName,
                formatterForMSG.format(pickupDate),
                dropOff.id.takeLast(4)
            )
        )
        intent.setPackage(whatsAppAppId)
        intent.putExtra(Intent.EXTRA_TEXT, text);
        val smsNumber = "+".plus(dropOff.clientPhone)
        intent.putExtra(
            "jid",
            "$smsNumber@s.whatsapp.net"
        ) //phone number without "+" prefix


        if (intent.resolveActivity(packageManager) == null) {
            Toast.makeText(requireContext(), "Whatsapp not installed.", Toast.LENGTH_SHORT)
                .show()
            return
        }
        startActivity(intent)
    }


    private fun setNewClient(dropOff: DropOff): Client? {
        var client: Client? = null
        if (viewModel.isChecked.value == true) {
            var id = ""
            if (dropOff.clientID == id) id = UUID.randomUUID().toString() else id = dropOff.clientID
            client = Client(
                id = id,
                name = dropOff.clientName,
                phone = dropOff.clientPhone,
                isCrew = dropOff.isCrew,
                plateNumber = dropOff.plateNumber,
                dateAdded = formatter.format(Date()),
                score = dropOff.score
            )
        }

        return client
    }


    private fun setNewDropOff(): DropOff {
        return DropOff(
            id = currentDropOff?.id ?: UUID.randomUUID().toString(),
            dateAdded = currentDropOff?.dateAdded ?: formatter.format(Date()),
            dateOUT = binding.txtDateOut.text.toString(),
            clientID = foundClient?.id ?: UUID.randomUUID().toString(),
            clientName = foundClient?.name ?: binding.txtClientName.text.toString(),
            plateNumber = foundClient?.plateNumber ?: binding.txtPlateNumber.text.toString(),
            clientPhone = foundClient?.phone ?: binding.txtClientPhone.text.toString(),
            parkingLot = binding.txtParkingLot.text.toString(),
            isCrew = binding.isCrewCheckBox.isChecked,
            serviceType = binding.spinServiceType.selectedItem.toString(),
            feeType = binding.spinFeeType.selectedItem.toString(),
            notes = binding.txtNotes.text.toString(),
            score = binding.scoreBar.rating
        )
    }

    private fun openDatePicker() {
        val now = Calendar.getInstance()
        val dpd =
            DatePickerDialog.newInstance(
                this,
                now[Calendar.YEAR],  // Initial year selection
                now[Calendar.MONTH],  // Initial month selection
                now[Calendar.DAY_OF_MONTH] // Inital day selection
            ).show(parentFragmentManager, "DatePickerDialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        selectedDateOut = StringBuilder().append(year).append("-")
            .append(monthOfYear + 1).append("-").append(dayOfMonth).toString()
        val now = Calendar.getInstance()
        val tpd =
            TimePickerDialog.newInstance(
                this,
                now[Calendar.HOUR],  // Initial year selection
                now[Calendar.MINUTE],  // Initial month selection
                true // Inital day selection
            ).show(parentFragmentManager, "TimePickerDialog")
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        selectedDateOut += StringBuilder().append(" ").append(hourOfDay).append(":").append(minute)
            .toString()
        Log.i(TAG, "onTimeSet: $selectedDateOut")
        val formatter = SimpleDateFormat(Constants.FOR_SQL)
        val _date = formatter.parse(selectedDateOut)
        binding.txtDateOut.setText(formatter.format(_date))
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        foundClient = parent?.getItemAtPosition(position) as Client
        binding.txtClientName.setText(foundClient?.name)
        binding.txtClientPhone.setText(foundClient?.phone)
    }

}