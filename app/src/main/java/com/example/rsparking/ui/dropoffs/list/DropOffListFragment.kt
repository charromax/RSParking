package com.example.rsparking.ui.dropoffs.list

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.os.Bundle
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.rsparking.R
import com.example.rsparking.data.model.DropOff
import com.example.rsparking.databinding.DropOffListFragmentBinding
import com.example.rsparking.ui.driver.addedit.PERMISSION_REQUEST_CODE
import com.example.rsparking.util.ToolbarInterface
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


const val FRAG_TITLE = "DropOffs List"

class DropOffListFragment : Fragment() {

    private lateinit var viewModel: DropOffListViewModel
    private lateinit var binding: DropOffListFragmentBinding
    private lateinit var adapter: DropOffListAdapter
    private lateinit var toolbarCallback: ToolbarInterface
    private val STORAGE_PERMISSION_CODE = 3
    private var tableFileName = ""
    private lateinit var storagePermission: Array<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        //TODO este fragmento es un chiquero
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        toolbarCallback = activity as ToolbarInterface
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        val menuInflater = requireActivity().menuInflater
        menuInflater.inflate(R.menu.main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView =
            searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                adapter.filter.filter(s.toLowerCase())
                return false
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (adapter.currentList.size > 0) {
            if (item.itemId == R.id.action_export) {
                if (checkStoragePermission()) {
                    exportCSVandShare()
                } else {
                    requestPermission()
                }
                return false
            }
        } else {
            Toast.makeText(requireContext(), "The list is empty!", Toast.LENGTH_SHORT).show()
            return super.onOptionsItemSelected(item)
        }
        return false
    }

    private fun checkStoragePermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            storagePermission,
            PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //permission granted
                exportCSVandShare()
            } else {
                //permission allowed
                Snackbar.make(requireView(), "Permission denied", Snackbar.LENGTH_SHORT)
            }
        }
    }

    private fun exportCSVandShare(): Boolean {
        val fullPathFilename = createCSVFile()

        val tableArray = makeTableArray()
        try {
            val fileWriter = FileWriter(fullPathFilename)
            for (row in tableArray) {
                for (i in row.indices) {
                    if (i == (row.size - 1)) {
                        fileWriter.append("${row[i]}\n")
                    } else {
                        fileWriter.append("${row[i]},")
                    }
                }
            }
            fileWriter.flush()
            fileWriter.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        shareFile()
        return true
    }

    private fun shareFile() {
        val fileLocation = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
            tableFileName
        )
        val path = FileProvider.getUriForFile(
            requireContext(),
            "com.example.rsparking.fileprovider",
            fileLocation
        )

        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/csv"
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        shareIntent.putExtra(Intent.EXTRA_STREAM, path)
        startActivity(
            Intent.createChooser(shareIntent, "Export table to:")
        )
    }

    private fun createCSVFile(): String {
        val timeStamp = SimpleDateFormat(
            "MMdd_HHmm",
            Locale.getDefault()
        ).format(Date())
        tableFileName = "CDO_$timeStamp.csv"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        return "$storageDir/$tableFileName"
    }

    private fun makeTableArray(): ArrayList<ArrayList<String>> {
        val headers =
            ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.drop_off_headers)))
        val data = ArrayList<DropOff>(adapter.currentList)
        var array = ArrayList<ArrayList<String>>()
        array.add(headers)
        for (item in data) {
            array.add(item.toArrayList())
        }
        return array
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.drop_off_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        toolbarCallback.getToolbarResources(FRAG_TITLE, 1)
        val viewModelFactory =
            DropOffViewModelFactory(
                application
            )
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(DropOffListViewModel::class.java)
        binding.listViewModel = viewModel
        binding.dropOffList.layoutManager =
            StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        setupAdapter()
        binding.dropOffList.adapter = adapter

        val itemTouchHelper = ItemTouchHelper(listTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.dropOffList)

        viewModel.allDropoffs.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.updateList(it)
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DropOffListFragmentDirections.actionDropOffListFragmentToAddEditDropOffFragment(
                        selectedDropOff = null
                    )
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToAddEditFragmentWithDropOff.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    DropOffListFragmentDirections.actionDropOffListFragmentToAddEditDropOffFragment(
                        selectedDropOff = it
                    )
                )
                viewModel.doneNavigatingWithID()
            }
        })
        viewModel.doneDeletingItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Snackbar.make(binding.root, R.string.on_item_deleted, Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        })
        viewModel.doneDeliveringCar.observe(viewLifecycleOwner, Observer {
            it?.let {
                Snackbar.make(binding.root, R.string.saved_succesfully, Snackbar.LENGTH_LONG)
                    .show()
            }
        })

        return binding.root
    }

    private fun setupAdapter() {
        adapter = DropOffListAdapter(object :
            DropOffListClickListener {
            override fun onClick(dropOff: DropOff) {
                viewModel.onListItemClicked(dropOff)
            }
        }, requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.dropOffList.adapter = null
    }

    private val listTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition

            if (direction == ItemTouchHelper.LEFT) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm delete")
                builder.setMessage(resources.getString(R.string.confirm_delete))
                builder.setPositiveButton(R.string.yes) { dialog, which ->
                    viewModel.onListItemSwipeRight()
                    viewModel.onConfirmDelete(position)
                }
                builder.setNegativeButton(R.string.no) { dialog, which ->
                    adapter.notifyItemChanged(position)
                    Toast.makeText(requireContext(), R.string.on_cancel_delete, Toast.LENGTH_SHORT)
                        .show()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Confirm deliver?")
                builder.setMessage(resources.getString(R.string.confirm_delivery))
                builder.setPositiveButton(R.string.yes) { dialog, which ->
                    viewModel.onListItemSwipeLeft()
                    viewModel.onConfirmDeliver(position)
                }
                builder.setNegativeButton(R.string.no) { dialog, which ->
                    adapter.notifyItemChanged(position)
                    Toast.makeText(requireContext(), R.string.on_cancel_delete, Toast.LENGTH_SHORT)
                        .show()
                }
                val alertDialog = builder.create()
                alertDialog.show()
            }

        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(
                c,
                recyclerView,
                viewHolder,
                dX,
                dY,
                actionState,
                isCurrentlyActive
            )
                .addSwipeLeftActionIcon(R.drawable.ic_delete_red)
                .addSwipeRightActionIcon(R.drawable.ic_check_green)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }
}