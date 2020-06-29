package com.example.rsparking.ui.driver.addedit

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.rsparking.R
import com.example.rsparking.data.model.Driver
import com.example.rsparking.databinding.DriverAddFragmentBinding
import com.example.rsparking.util.Constants
import com.example.rsparking.util.ToolbarInterface
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CAMERA = 1
const val FRAG_TITLE = "Add/Edit Driver"
const val PERMISSION_REQUEST_CODE = 101

class AddEditDriverFragment : Fragment() {
    private var currentDriver: Driver? = null
    private lateinit var binding: DriverAddFragmentBinding
    private lateinit var viewModelAddEdit: AddEditDriverViewModel
    val args: AddEditDriverFragmentArgs by navArgs()
    private val formatter = SimpleDateFormat(Constants.FOR_SQL)
    private var driverIDArg: String? = null
    private var currentPhotoPath = ""
    private var driverImagePath: String = ""
    private lateinit var photoURI: Uri
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
        binding = DataBindingUtil.inflate(inflater, R.layout.driver_add_fragment, container, false)
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application

        args.selectedDriver?.let {
            driverIDArg = it.id
            currentDriver = it
        }
        val viewModelFactory =
            AddEditDriverViewModelFactory(
                driverIDArg,
                application
            )
        toolbarCallback.getToolbarResources(FRAG_TITLE, 1)

        viewModelAddEdit = ViewModelProviders.of(this, viewModelFactory)
            .get(AddEditDriverViewModel::class.java)
        binding.driverViewModel = viewModelAddEdit

        viewModelAddEdit.saveDriverEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                currentDriver?.let {
                    driverImagePath = it.image
                    viewModelAddEdit.updateDriver()
                    viewModelAddEdit.doneUpdating()
                } ?: viewModelAddEdit.saveDriver(setNewDriver())

                viewModelAddEdit.doneSaving()
                Snackbar.make(this.requireView(), R.string.saved_succesfully, Snackbar.LENGTH_SHORT)
                    .show()
                this.findNavController().navigateUp()
            }
        })

        viewModelAddEdit.openCameraEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (!checkPersmission()) {
                    requestPermission()
                } else {
                    openCameraIntent()
                    viewModelAddEdit.doneWithCamera()
                }
            }
        })
        viewModelAddEdit.currentdriver.observe(viewLifecycleOwner, Observer {
            Log.i("FRAG_TITLE", "driver name: ${it.name}")
        })


        return binding.root
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.CAMERA
        ) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(READ_EXTERNAL_STORAGE, CAMERA),
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED
                ) {
                    openCameraIntent()
                    viewModelAddEdit.doneWithCamera()
                } else {
                    Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun openCameraIntent() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.resolveActivity(requireActivity().packageManager)?.let {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(
                    requireActivity().applicationContext,
                    "com.example.rsparking.fileprovider",
                    photoFile
                )
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(
                    cameraIntent,
                    REQUEST_CAMERA
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == Activity.RESULT_OK) {
            val cropURI = Uri.fromFile(File(currentPhotoPath))
            UCrop.of(cropURI, cropURI)
                .withAspectRatio(1F, 1F)
                .withMaxResultSize(400, 400)
                .start(requireContext(), this, UCrop.REQUEST_CROP)
        } else if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri = UCrop.getOutput(data!!)!!
            Glide.with(this)
                .load(resultUri)
                .into(binding.imgPhoto)
        } else {
            if (resultCode == UCrop.RESULT_ERROR) {
                val cropError: Throwable = UCrop.getError(data!!)!!
            }
        }

    }


    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat(
            "yyyyMMdd_HHmmss",
            Locale.getDefault()
        ).format(Date())
        val imageFileName = "DRV_" + timeStamp + "_"
        val storageDir =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        currentPhotoPath = image.absolutePath
        driverImagePath = currentPhotoPath
        return image

    }

    private fun setNewDriver(): Driver {
        return Driver(
            id = currentDriver?.id ?: UUID.randomUUID().toString(),
            name = binding.txtName.text.toString(),
            lastName = binding.txtLastName.text.toString(),
            phone = binding.txtPhone.text.toString(),
            eMail = binding.txtEmail.text.toString(),
            image = driverImagePath,
            dateAdded = currentDriver?.dateAdded ?: formatter.format(Date())
        )
    }

}