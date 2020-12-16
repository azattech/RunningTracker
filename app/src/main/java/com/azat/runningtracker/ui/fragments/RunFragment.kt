package com.azat.runningtracker.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.azat.runningtracker.R
import com.azat.runningtracker.adapters.RunAdapter
import com.azat.runningtracker.other.*
import com.azat.runningtracker.other.Constants.Companion.REQUEST_CODE_LOCATION_PERMISSION
import com.azat.runningtracker.ui.viewmodel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_run.*
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 22/06/2020 - 10:31 PM *
 ************************/
@AndroidEntryPoint
class RunFragment : Fragment(R.layout.fragment_run), EasyPermissions.PermissionCallbacks {

    lateinit var runAdapter: RunAdapter

    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel = (activity as MainActivity).mainViewModel
        runAdapter = RunAdapter()

        setupRecyclerView()
        // requestPermissions()
        fab.setOnClickListener {
            askPermissions()
        }

        when (viewModel.sortType) {
            SortType.DATE -> spFilter.setSelection(0)
            SortType.RUNNING_TIME -> spFilter.setSelection(1)
            SortType.DISTANCE -> spFilter.setSelection(2)
            SortType.AVG_SPEED -> spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> spFilter.setSelection(4)
        }
        viewModel.runs.observe(viewLifecycleOwner, Observer { runs ->
            runAdapter.submitList(runs)
        })

        spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}

            override fun onItemSelected(
                adapterView: AdapterView<*>?,
                view: View?,
                pos: Int,
                id: Long
            ) {
                when (pos) {
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.DISTANCE)
                    3 -> viewModel.sortRuns(SortType.AVG_SPEED)
                    4 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
            }
        }
    }

    /**
     * Handles swipe-to-delete
     */
    private val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
        ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return true
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition
            val run = runAdapter.differ.currentList[position]
            viewModel.deleteRun(run)
            Snackbar.make(requireView(), "Successfully deleted run", Snackbar.LENGTH_LONG).apply {
                setAction("Undo") {
                    viewModel.insertRun(run)
                }
                show()
            }
        }
    }

    private fun setupRecyclerView() = rvRuns.apply {
        adapter = runAdapter
        layoutManager = LinearLayoutManager(activity)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
    }

    private fun askPermissions() {
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                if (checkLocationPermissionAPI28(REQUEST_CODE_LOCATION_PERMISSION)) {
                    findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
                }
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                if (checkLocationPermissionAPI29(REQUEST_CODE_LOCATION_PERMISSION)) {
                    findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
                }
            }
            else -> {
                if (checkBackgroundLocationPermissionAPI30(REQUEST_CODE_LOCATION_PERMISSION)) {
                    findNavController().navigate(R.id.action_runFragment_to_trackingFragment)
                }
            }

        }
    }

    private fun requestPermissions() {
        if (TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        when {
            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                EasyPermissions.requestPermissions(
                    this,
                    "You need to accept location permission to track your distance and route.",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }
            Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
                EasyPermissions.requestPermissions(
                    this,
                    "To track your distance and route, the app also needs to access your location while app is in the background. Please enable 'Allow all the time' location permission. You can change this permission in the device settings.",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
            else -> {
                EasyPermissions.requestPermissions(
                    this,
                    "This app doesn't use your any data. To track your distance and route, the app also needs to access your location while app is in the background. Please enable 'Allow all the time' location permission. You can change this permission in the device settings.",
                    REQUEST_CODE_LOCATION_PERMISSION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).setThemeResId(R.style.AlertDialogTheme).build().show()
        } else {
            // requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }
}