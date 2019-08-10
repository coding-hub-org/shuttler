package com.psucoders.shuttler.ui.track


import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.dashboard.DashboardActivity
import com.psucoders.shuttler.utils.adapters.StopAdapter
import com.psucoders.shuttler.utils.helpers.AnimationMarkerHelper
import com.psucoders.shuttler.utils.helpers.Spherical
import kotlinx.android.synthetic.main.track_fragment.*

@Suppress("PrivatePropertyName")
class TrackFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = TrackFragment()
    }

    // LOCATION
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mCurrentLocation: Location


    private lateinit var trackViewModel: TrackViewModel
    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View
    private val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2310
    private val stopList = arrayListOf(
            LatLng(44.692358, -73.486985),
            LatLng(44.703424, -73.492683),
            LatLng(44.695382, -73.492342),
            LatLng(44.698919, -73.476508))

    private lateinit var db: FirebaseFirestore
    private val markersHashMap:HashMap<String,Marker> = HashMap()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.track_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        trackViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(TrackViewModel::class.java)

        // Recycler view
        val testArr = arrayListOf("Walmart", "Target", "Campus")
        val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recycler_view_stops)
        recyclerView?.layoutManager = layoutManager
        val adapter = StopAdapter(testArr)
        recyclerView?.adapter = adapter

        (activity as DashboardActivity).supportActionBar?.title = getString(R.string.title_activity_tracker)

        // LOCATION
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context!!)
        buildLocationRequest()
        buildLocationCallback()


        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapView = mapFragment.view!!

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance()

        button_update_rv.setOnClickListener {
            testArr.add(testArr.size ,testArr[0])
            testArr.removeAt(0)
            adapter.notifyItemRemoved(0)
            adapter.notifyItemInserted(testArr.size)
        }
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
                        if (markersHashMap[document.id] != null) {

//                            if (markersHashMap[document.id] != null && document.data["bearing"] != null) {
//                            markersHashMap[document.id]!!.rotation = (document.data["bearing"] as Double).toFloat()
                            AnimationMarkerHelper.animateMarkerToGB(markersHashMap[document.id]!!,
                                    LatLng((document.data["location"] as GeoPoint).latitude,
                                            (document.data["location"] as GeoPoint).longitude),
                                    Spherical())
                        } else {
                            markersHashMap[document.id] = mMap.addMarker(MarkerOptions()
                                    .position(LatLng((document.data["location"] as GeoPoint).latitude,
                                            (document.data["location"] as GeoPoint).longitude))
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle_car_ic))
                                    .flat(true)
                                    .anchor(0.5f, 0.5f)
                                    .title("Stop"))
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("RESULT EXCEPTION", "Error getting documents.", exception)
                }

        if (ContextCompat.checkSelfPermission
                (context!!, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient
                    .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isIndoorEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.698974, -73.477146), 14f))
        db.collection("drivers")
                .addSnapshotListener(EventListener<QuerySnapshot> { snapshots, e ->
                    if (e != null) {
                        Log.w("FAILED SNAPSHOT", "Listen failed.", e)
                        return@EventListener
                    }
                    for (dc in snapshots!!.documentChanges) {
                        if (dc.type == DocumentChange.Type.MODIFIED) {

                            if (dc.document.data["active"] == true) {
//                                Toast.makeText(context, "STATUS ACTIVE ${dc.document.data["active"]}", Toast.LENGTH_LONG).show()
                                if (markersHashMap[dc.document.id] != null) {

//                                    if (markersHashMap[dc.document.id] != null && dc.document.data["bearing"] != null) {
//                                    markersHashMap[dc.document.id]!!.rotation = (dc.document.data["bearing"] as Double).toFloat()
                                    AnimationMarkerHelper.animateMarkerToGB(markersHashMap[dc.document.id]!!,
                                            LatLng((dc.document.data["location"] as GeoPoint).latitude,
                                                    (dc.document.data["location"] as GeoPoint).longitude),
                                            Spherical())
                                } else {
                                    markersHashMap[dc.document.id] = mMap.addMarker(MarkerOptions()
                                            .position(LatLng((dc.document.data["location"] as GeoPoint).latitude,
                                                    (dc.document.data["location"] as GeoPoint).longitude))
                                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle_car_ic))
                                            .flat(true)
                                            .anchor(0.5f, 0.5f)
                                            .title("Stop"))
                                }
                            } else {
//                                Toast.makeText(context, "STATUS INACTIVE ${dc.document.data["active"]}", Toast.LENGTH_LONG).show()
                                if (markersHashMap[dc.document.id] != null) {
                                    markersHashMap[dc.document.id]!!.remove()
                                    markersHashMap.remove(dc.document.id)
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


    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        Toast.makeText(context, "destroyed", Toast.LENGTH_LONG).show()
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

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult!!.lastLocation


//                val bearing = mPreviousLocation.bearingTo(mCurrentLocation)
//                testMarker!!.rotation = bearing
//
//                testMarker = mMap.addMarker(MarkerOptions()
//                        .position(LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude))
//                        .title("Stop")
//                        .flat(true)
//                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.shuttle_car_ic))
//                        .anchor(0.5f, 0.5f)
//                        .rotation(bearing))*/

//                val posTest = LatLng(mCurrentLocation.latitude, mCurrentLocation.longitude)
//                testMarker!!.position = posTest
//                testMarker!!.rotation = bearing

                Log.d("LOCATION RESULT DBUG", locationResult.toString())
                Toast.makeText(context, "CURRENT LATITUDE: ${mCurrentLocation.latitude}   CURRENT LONGITUDE: ${mCurrentLocation.longitude}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
