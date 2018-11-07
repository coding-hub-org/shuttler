package com.psucoders.geolocationandroid

import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*

class GeolocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CODE = 1000

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this@GeolocationActivity, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this@GeolocationActivity, "NOTs PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_geolocation)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        // Check permission
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else {
            buildLocationRequest()
            buildLocationCallBack()

            // Create FusedLocationProvider
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            if (ActivityCompat.checkSelfPermission(this@GeolocationActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this@GeolocationActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this@GeolocationActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
                return
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

    }

    private fun buildLocationCallBack() {
        locationCallback = object :LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                // Get last location
                val location = p0!!.locations[p0.locations.size-1]
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                val yourPosition = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(yourPosition).title("YOU"))
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourPosition, 14f))
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
}
