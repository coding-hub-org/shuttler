package com.psucoders.shuttler

import android.app.Activity
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_driver.*
import org.jetbrains.anko.toast

class DriverActivity : AppCompatActivity() {


     private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2310

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        // Set event
        switchDuty.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                toast("ON DUTY")
                startService()
            } else {
                toast("OFF DUTY")
                stopService()
            }
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, DriverService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, DriverService::class.java)
        stopService(serviceIntent)
    }


    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (this@DriverActivity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(this@DriverActivity)
                    .setTitle("Location needed")
                    .setMessage("In order to provide you with the best user experience (Driver) " +
                            "we need to access your device location")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("CANCEL") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        try {
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@DriverActivity, "Permission granted", Toast.LENGTH_LONG).show()
                } else {
                    // Send to other activity
                    Toast.makeText(this@DriverActivity, "Need permission", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(this@DriverActivity, "Can't get user location permission", Toast.LENGTH_LONG).show()
        }
    }
}
