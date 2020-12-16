package com.azat.runningtracker.other

import android.Manifest
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 12/12/2020 - 2:19 PM  *
 ************************/

private fun Fragment.checkSinglePermission(permission: String) =
    ContextCompat.checkSelfPermission(
        requireContext(),
        permission
    ) == PackageManager.PERMISSION_GRANTED

/**
 * Build.VERSION_CODES.P
 * */
@TargetApi(28)
fun Fragment.checkLocationPermissionAPI28(locationRequestCode: Int): Boolean {
    return if (!checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
        !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    ) {
        val permList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermissions(permList, locationRequestCode)
        false
    } else {
        true
    }
}

/**
 * Build.VERSION_CODES.Q
 * */
@TargetApi(29)
fun Fragment.checkLocationPermissionAPI29(locationRequestCode: Int): Boolean {
    if (!checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
        !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
        !checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    ) {
        AlertDialog.Builder(requireContext())
            .setTitle("Need your location ")
            .setMessage(
                "To track your distance and route, we also need to access your location while the app is in the background" +
                        " You can change this permission in the device settings."
            )
            .setCancelable(false)
            .setPositiveButton("Allow") { _, _ ->
                val permList = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
                requestPermissions(permList, locationRequestCode)
            }
            .setNegativeButton("Deny") { dialog, _ ->
                dialog.dismiss()
                okDialog()
            }
            .create()
            .show()
        return false
    } else {
        if (!checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            AlertDialog.Builder(requireContext())
                .setTitle("Need your location ")
                .setMessage(
                    "To track your distance and route, we also need to access your location while the app is in the background" +
                            " You can change this permission in the device settings."
                )
                .setCancelable(false)
                .setPositiveButton("Allow") { _, _ ->
                    val permList = arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                    requestPermissions(permList, locationRequestCode)
                }
                .setNegativeButton("Deny") { dialog, _ ->
                    dialog.dismiss()
                    okDialog()
                }
                .create()
                .show()
            return false
        }
        return true
    }
}

/**
 * Build.VERSION_CODES.R
 * */
@TargetApi(30)
fun Fragment.checkBackgroundLocationPermissionAPI30(backgroundLocationRequestCode: Int): Boolean {
    if (!checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
        AlertDialog.Builder(requireContext())
            .setTitle("Need your location ")
            .setMessage(
                "To track your distance and route, we also need to access your location while the app is in the background" +
                        " You can change this permission in the device settings."
            )
            .setCancelable(false)
            .setPositiveButton("Yes") { _, _ ->
                // this request will take user to Application's Setting page
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                //  val uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID)
                val uri: Uri = Uri.fromParts("package", requireContext().packageName, null)
                intent.data = uri
                startActivity(intent)
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                    backgroundLocationRequestCode
                )
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
        return false
    } else {
        return true
    }
}

fun Fragment.okDialog() {
    AlertDialog.Builder(requireContext())
        .setTitle("Location Denied")
        .setMessage(
            "In order for the app to track your distance and route, the app needs additional access to your location. " +
                    "You can change this permission in the device settings."
        )
        .setCancelable(false)
        .setPositiveButton("Ok") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

fun Fragment.checkBackgroundLocationPermission(locationRequestCode: Int) {
    when {
        Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
            checkLocationPermissionAPI28(locationRequestCode)
        }
        Build.VERSION.SDK_INT == Build.VERSION_CODES.Q -> {
            checkLocationPermissionAPI29(locationRequestCode)
        }
        else -> {
            checkBackgroundLocationPermissionAPI30(locationRequestCode)
        }
    }
}