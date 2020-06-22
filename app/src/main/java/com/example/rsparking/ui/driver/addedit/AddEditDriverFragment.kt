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
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_CAMERA = 1
const val FRAG_TITLE = "Add/Edit Fragment"
const val PERMISSION_REQUEST_CODE = 101

class AddEditDriverFragment : Fragment() {
    private lateinit var binding: DriverAddFragmentBinding
    private lateinit var viewModelAddEdit: AddEditDriverViewModel
    val args: AddEditDriverFragmentArgs by navArgs()
    private val formatter = SimpleDateFormat(Constants.FOR_SQL)
    private var driverIDArg: String = ""
    private var currentPhotoPath = ""
    private var driverImagePath: String = ""
    private lateinit var photoURI: Uri


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.driver_add_fragment, container, false)
        binding.lifecycleOwner = this
        val actionbar = requireActivity().actionBar
        actionbar?.title = FRAG_TITLE
        val application = requireNotNull(this.activity).application
        args.driverID?.let {
            driverIDArg = it
        }
        val viewModelFactory =
            AddEditDriverViewModelFactory(
                driverIDArg,
                application
            )

        viewModelAddEdit = ViewModelProviders.of(this, viewModelFactory)
            .get(AddEditDriverViewModel::class.java) // ver como se hace bien esto
        binding.driverViewModel = viewModelAddEdit

        viewModelAddEdit.saveDriverEvent.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (driverIDArg == "") {
                    viewModelAddEdit.saveDriver(setNewDriver())
                } else {
                    viewModelAddEdit.incomingDriverImage.observe(
                        viewLifecycleOwner,
                        Observer { image ->
                            driverImagePath = image
                            viewModelAddEdit.updateDriver(setNewDriver())
                            //TODO no actualiza datos
                        })

                }
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
        viewModelAddEdit.driver.observe(viewLifecycleOwner, Observer {
            Log.i("FRAG_TITLE", "driver image: ${it.image}")
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

            else -> {

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
            if (driverIDArg != "") driverIDArg else UUID.randomUUID().toString(),
            binding.txtName.text.toString(),
            binding.txtLastName.text.toString(),
            binding.txtPhone.text.toString(),
            binding.txtEmail.text.toString(),
            driverImagePath,
            formatter.format(Date())
        )
    }

}