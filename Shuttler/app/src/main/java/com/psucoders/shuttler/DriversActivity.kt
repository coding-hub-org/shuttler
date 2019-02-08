package com.psucoders.shuttler

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.drivers_activity.*
import org.jetbrains.anko.toast

class DriversActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()!!

    private lateinit var drivers: DatabaseReference
    private lateinit var geoFire: GeoFire

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUESTCODE = 2310

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUESTCODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this@DriversActivity, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this@DriversActivity, "NOTs PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drivers_activity)

        // Geofire
        drivers = FirebaseDatabase.getInstance().getReference("Drivers")
        geoFire = GeoFire(drivers)

        // Check permissions
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUESTCODE)
        else {
            buildLocationRequest()
            buildLocationCallback()
            // Create FusedProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            // Set event
            switchDuty.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    toast("ON DUTY")
                    if (ActivityCompat.checkSelfPermission(this@DriversActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this@DriversActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@DriversActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUESTCODE)
                        return@setOnCheckedChangeListener
                    }

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
                }
                else {
                    toast("OFF DUTY")
                    if (ActivityCompat.checkSelfPermission(this@DriversActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this@DriversActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@DriversActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUESTCODE)
                        return@setOnCheckedChangeListener
                    }

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object :LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                // Get last location
                val location = p0!!.locations[p0.locations.size - 1]
                Toast.makeText(this@DriversActivity, "LATITUDE: ${location.latitude}  LONGITUDE: ${location.longitude}", Toast.LENGTH_LONG).show()
                geoFire.setLocation(FirebaseAuth.getInstance().currentUser!!.uid, GeoLocation(location.latitude, location.longitude)) { _, _ ->
                    return@setLocation
                }
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    override fun onStart() {
        super.onStart()

        // Logout driver
        btnLogoutDriver.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
