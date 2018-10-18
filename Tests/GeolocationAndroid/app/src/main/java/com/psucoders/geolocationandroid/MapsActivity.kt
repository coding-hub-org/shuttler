package com.psucoders.geolocationandroid

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : PermissionsActivity(), OnMapReadyCallback {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View
    lateinit var geofencingClient: GeofencingClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        checkLocationPermission()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        button.setOnClickListener {
            fusedLocationClient.lastLocation
                    .addOnSuccessListener { location : Location? ->
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            toast(location.latitude.toString() + " " + location.longitude.toString())
                        }
                        else {
                            toast("LOCATION NOT AVAILABLE")
                        }
                    }
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Polylines are useful for marking paths and routes on the map.
        mMap.addPolyline(PolylineOptions().geodesic(true)
                .add(LatLng(-33.866, 151.195))  // Sydney
                .add(LatLng(-18.142, 178.431))  // Fiji
                .add(LatLng(21.291, -157.821))  // Hawaii
                .add(LatLng(37.423, -122.091))  // Mountain View
        )

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    /*
    * EVERYTHING TO DO WITH PERMISSIONS
    * If granted, gets location. Else displays message to ask again. If not granted, app exits
    * */
    private fun checkLocationPermission() {
        requestPermissions((Manifest.permission.ACCESS_FINE_LOCATION), object : PermissionCallBack {
            @SuppressLint("MissingPermission")
            override fun permissionGranted() {
                super.permissionGranted()
                mMap.isMyLocationEnabled = true

            }

            override fun permissionDenied() {
                super.permissionDenied()
                val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AlertDialog.Builder(this@MapsActivity, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    AlertDialog.Builder(this@MapsActivity)
                }
                builder.setTitle("Permissions Required")
                        .setMessage("We require permissions to provide you with the best experience of the app")
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            checkLocationPermission()
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            finish()
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
            }
        })
    }


}
