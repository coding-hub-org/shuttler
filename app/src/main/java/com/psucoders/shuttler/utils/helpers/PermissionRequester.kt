package com.psucoders.shuttler.utils.helpers

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.psucoders.shuttler.ui.driver.DriverActivity

class PermissionRequester : ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2310
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        try {
            if (requestCode == DriverActivity.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
            }
        } catch (e: SecurityException) {
            // Permissions not granted
            e.printStackTrace()
        }
    }

    fun requestLocationPermission(activity: Activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(activity)
                    .setTitle("Location needed")
                    .setMessage("In order to provide you with the best user experience (Driver) " +
                            "we need to access your device location")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("CANCEL") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }
}