package com.psucoders.shuttler

import android.app.Activity
 import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
 import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
 import androidx.core.content.ContextCompat



class TrackFragment : Fragment(), OnMapReadyCallback {

    companion object {
        fun newInstance() = TrackFragment()
    }

    private lateinit var viewModel: TrackViewModel
    private lateinit var mMap: GoogleMap

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
        mapFragment.getMapAsync(this)
        // TODO: Use the ViewModel
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.isIndoorEnabled = true

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(44.693218, -73.465782)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

}
