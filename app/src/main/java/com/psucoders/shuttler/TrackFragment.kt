package com.psucoders.shuttler

import android.app.Activity
import android.app.AlertDialog
 import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import com.google.firebase.firestore.*

@Suppress("PrivatePropertyName")
class TrackFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = TrackFragment()
    }

    private lateinit var viewModel: TrackViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2310
    private val stopList = arrayListOf(
            LatLng(44.692358, -73.486985),
            LatLng(44.703424, -73.492683),
            LatLng(44.695382, -73.492342),
            LatLng(44.698919, -73.476508))

    private lateinit var db: FirebaseFirestore
    private val markersHashMap:HashMap<String,Marker> = HashMap() //define empty hashmap


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

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()

        mapFragment.getMapAsync(this)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        // TODO: Check permissions onStart
        super.onStart()

        // TODO: Might move to onCreate
        db.collection("drivers")
                .whereEqualTo("active", true)
                .get()
                .addOnSuccessListener { result ->

                    for (document in result) {
                        Toast.makeText(context, "RESULTS FIRESTORE ${result.size()}", Toast.LENGTH_LONG).show()
                        if (markersHashMap[document.id] != null) {
                            markersHashMap[document.id]!!.remove()
                        }
                        markersHashMap[document.id] = mMap.addMarker(MarkerOptions()
                                .position(LatLng((document.data["location"] as GeoPoint).latitude,
                                        (document.data["location"] as GeoPoint).longitude))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle_car_ic))
                                .title("Stop"))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("RESULT EXCEPTION", "Error getting documents.", exception)
                }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isIndoorEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        db.collection("drivers")
//                .whereEqualTo("active", true)
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w("FAILED SNAPSHOT", "Listen failed.", e)
                        return@EventListener
                    }
                    lateinit var geoPoint: GeoPoint
                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.MODIFIED) {

                            if (dc.document.data["active"] == true) {
                                Toast.makeText(context, "STATUS ACTIVE ${dc.document.data["active"]}", Toast.LENGTH_LONG).show()
                                if (markersHashMap[dc.document.id] != null) {
                                    markersHashMap[dc.document.id]!!.remove()
                                }
                                markersHashMap[dc.document.id] = mMap.addMarker(MarkerOptions()
                                        .position(LatLng((dc.document.data["location"] as GeoPoint).latitude,
                                                (dc.document.data["location"] as GeoPoint).longitude))
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle_car_ic))
                                        .title("Stop"))
                            } else {
                                Toast.makeText(context, "STATUS INACTIVE ${dc.document.data["active"]}", Toast.LENGTH_LONG).show()
                                if (markersHashMap[dc.document.id] != null) {
                                    markersHashMap[dc.document.id]!!.remove()
                                }
                            }
                        }
                    }
                })

        if (ContextCompat.checkSelfPermission
                (context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.isMyLocationEnabled = true
        } else {
            requestLocationPermission()
        }

        if (mapView.findViewById<View>(Integer.parseInt("1")) != null) {
            // Get the button view
            val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<ImageView>(Integer.parseInt("2"))
            // and next place it, on bottom right (as Google Maps app)
            val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
            // position on right bottom
            locationButton.setImageResource(R.drawable.mylocation_ic)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0)
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE)
            layoutParams.setMargins(0, 0, 0, 30)
            locationButton.layoutParams = layoutParams
        }
        addStopMarkers(stopList)
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
