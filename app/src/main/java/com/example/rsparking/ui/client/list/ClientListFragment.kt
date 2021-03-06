package com.example.rsparking.ui.client.list

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
import com.example.rsparking.R
import com.example.rsparking.data.model.Client
import com.example.rsparking.databinding.ClientListFragmentBinding
import com.example.rsparking.ui.driver.addedit.PERMISSION_REQUEST_CODE
import com.example.rsparking.util.ToolbarInterface
import com.google.android.material.snackbar.Snackbar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val FRAG_TITLE = "Client List"
class ClientListFragment: Fragment() {

    private lateinit var viewModel: ClientListViewModel
    private lateinit var binding: ClientListFragmentBinding
    private lateinit var adapter: ClientListAdapter
    private lateinit var toolbarCallback: ToolbarInterface
    private val STORAGE_PERMISSION_CODE = 4
    private var tableFileName = ""
    private lateinit var storagePermission: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        toolbarCallback = activity as ToolbarInterface
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        tableFileName = "CNT_$timeStamp.csv"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)

        return "$storageDir/$tableFileName"
    }

    private fun makeTableArray(): ArrayList<ArrayList<String>> {
        val headers =
            ArrayList<String>(Arrays.asList(*resources.getStringArray(R.array.client_headers)))
        val data = ArrayList<Client>(adapter.currentList)
        var array = ArrayList<ArrayList<String>>()
        array.add(headers)
        for (item in data) {
            array.add(item.toArrayList())
        }
        return array
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.client_list_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        val application = requireNotNull(this.activity).application
        val viewModelFactory =
            ClientListViewModelFactory(
                application
            )
        toolbarCallback.getToolbarResources(FRAG_TITLE, 1)
        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(ClientListViewModel::class.java)
        binding.listViewModel = viewModel
        setupAdapter()
        binding.clientList.adapter = adapter
        val itemTouchHelper = ItemTouchHelper(listTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.clientList)

        viewModel.allClients.observe(viewLifecycleOwner, Observer {
            it?.let {
                adapter.submitList(it)
            }
        })

        viewModel.navigateToAddEditFragment.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    ClientListFragmentDirections.actionClientListFragmentToAddEditClientFragment(
                        null
                    )
                )
                viewModel.doneNavigating()
            }
        })
        viewModel.navigateToAddEditFragmentWithClient.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(
                    ClientListFragmentDirections.actionClientListFragmentToAddEditClientFragment(it)
                )
                viewModel.doneNavigatingWithID()
            }
        })
        viewModel.doneDeletingItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                if (it) {
                    Snackbar.make(binding.root, R.string.on_item_deleted, Snackbar.LENGTH_LONG)
                        .setAction("UNDO", {
                            viewModel.putBackIntoList()
                        }).show()
                }
            }
        })

        return binding.root
    }

    private fun setupAdapter() {
        adapter = ClientListAdapter(object :
            ClientListClickListener {
            override fun onClick(client: Client) {
                viewModel.onListItemClicked(client)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.clientList.adapter = null
    }

    private val listTouchHelperCallback = object :
        ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.adapterPosition
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
                .addSwipeRightActionIcon(R.drawable.ic_delete_red)
                .create()
                .decorate()
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}