package com.azat.runningtracker.other

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

/*************************
 * Created by AZAT SAYAN *
 *                       *
 * Contact: @theazat     *
 *                       *
 * 12/12/2020 - 2:19 PM  *
 ************************/

private fun Context.checkSinglePermission(permission: String) =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

/**
 * Build.VERSION_CODES.P
 * */
@TargetApi(28)
fun Context.checkLocationPermissionAPI28(locationRequestCode: Int) {
    if (!checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) ||
        !checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    ) {
        val permList = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        requestPermissions(this as Activity, permList, locationRequestCode)
    }
}

/**
 * Build.VERSION_CODES.Q
 * */
@TargetApi(29)
fun Context.checkLocationPermissionAPI29(locationRequestCode: Int) {
    if (checkSinglePermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
        checkSinglePermission(Manifest.permission.ACCESS_COARSE_LOCATION) &&
        checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    ) return
    val permList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )
    requestPermissions(this as Activity, permList, locationRequestCode)
}

/**
 * Build.VERSION_CODES.R
 * */
@TargetApi(30)
fun Context.checkBackgroundLocationPermissionAPI30(backgroundLocationRequestCode: Int) {
    if (checkSinglePermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) return
    AlertDialog.Builder(this)
        .setTitle("Title")
        .setMessage("You need to accept location permission to use this app")
        .setPositiveButton("Yes") { _, _ ->
            // this request will take user to Application's Setting page
            requestPermissions(
                this as Activity,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                backgroundLocationRequestCode
            )
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

fun Context.checkBackgroundLocationPermission(locationRequestCode: Int) {
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