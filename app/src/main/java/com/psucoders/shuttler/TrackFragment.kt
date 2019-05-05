package com.psucoders.shuttler

import android.app.Activity
import android.app.AlertDialog
 import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
 import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import android.widget.RelativeLayout
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class TrackFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = TrackFragment()
    }

    private lateinit var viewModel: TrackViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View
    @Suppress("PrivatePropertyName")
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2310
    val stopList = arrayListOf(LatLng(44.692358, -73.486985), LatLng(44.703424, -73.492683))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.track_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(TrackViewModel::class.java)

        // Recycler view
        val testArr = arrayListOf("Walmart", "Target", "Campus")
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view_stops)
        recyclerView?.layoutManager = layoutManager
        val adapter = StopAdapter(testArr)
        recyclerView?.adapter = adapter


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView = mapFragment.view!!
        mapFragment.getMapAsync(this)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        // TODO: Check permissions onStart
        super.onStart()
        Toast.makeText(context, "RESTART", Toast.LENGTH_LONG).show()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isIndoorEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        handleLocationPermission()


        if (mapView != null && mapView.findViewById<View>(Integer.parseInt("1")) != null) {
            // Get the button view
            val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<ImageView>(Integer.parseInt("2"))
            // and next place it, on bottom right (as Google Maps app)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            // position on right bottom
            locationButton.setImageResource(R.drawable.mylocation_ic)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 0, 30)
        }

        addStopMarkers(stopList)

    }

    private fun handleLocationPermission() {
        if (ContextCompat.checkSelfPermission
                (context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true

        } else {
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (activity as Activity, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            AlertDialog.Builder(context)
                    .setTitle("Location needed")
                    .setMessage("In order to provide you with the best user experience " +
                            "we need to access your device location")
                    .setPositiveButton("OK") { _, _ ->
                         requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("CANCEL") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
        } else {
            requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        try {
            if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.isMyLocationEnabled = true
                 } else {
                    // Send to other activity
                    Toast.makeText(context, "Need permission", Toast.LENGTH_LONG).show()
                }
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Can't get user location permission", Toast.LENGTH_LONG).show()
        }
    }

    private fun addStopMarkers(stopList: ArrayList<LatLng>) {
        for (stop in stopList) {
            addMarker(stop)
        }
    }

    private fun addMarker(coordinates: LatLng) {
         mMap.addMarker(MarkerOptions()
                .position(coordinates)
                 .title("Stop")
                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.stop_marker_ic)))
    }
}
