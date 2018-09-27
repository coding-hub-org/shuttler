package com.psucoders.shuttler

import android.content.res.Resources
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_tracker.*
import java.lang.Exception


class TrackerActivity : AppCompatActivity(), OnMapReadyCallback, Animation.AnimationListener {


    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        val toolbar: Toolbar = findViewById(R.id.toolbar_activity_tracker)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Shuttle Status"
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json))

            if (!success) {
                Log.e("MAPS FAIL", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("STYLE FAIL", "Can't find style. Error: ", e)
        }


        // Add a marker in Sydney and move the camera
        val plattsburgh = LatLng(44.693255, -73.475114)

        mMap.addMarker(MarkerOptions().position(plattsburgh).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(plattsburgh))
        try {

            mMap.setOnMapClickListener {
                //Warning because need to work on Landscape mode as well
                if (constraintLayoutBottomCard.visibility == View.INVISIBLE) {
                    val mAlphaAnim = AnimationUtils.loadAnimation(this, R.anim.fade_in_screen_from_bottom)
                    val mAlphaAnim2 = AnimationUtils.loadAnimation(this, R.anim.fade_in_screen_from_top)
                    mAlphaAnim.setAnimationListener(this)
                    constraintLayoutBottomCard.startAnimation(mAlphaAnim)
                    toolbar_activity_tracker.startAnimation(mAlphaAnim2)
                } else {
                    val mAlphaAnim = AnimationUtils.loadAnimation(this, R.anim.fade_out_of_screen_top)
                    val mAlphaAnim2 = AnimationUtils.loadAnimation(this, R.anim.fade_out_of_screen_bottom)
                    mAlphaAnim.setAnimationListener(this)
                    constraintLayoutBottomCard.startAnimation(mAlphaAnim2)
                    toolbar_activity_tracker.startAnimation(mAlphaAnim)
                }

            }
        } catch (e: Exception) {
            //Landscape mode.. Need to work on
        }
    }

    //Some animations
    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        if (constraintLayoutBottomCard.visibility == View.INVISIBLE) {
            constraintLayoutBottomCard.visibility = View.VISIBLE
            toolbar_activity_tracker.visibility = View.VISIBLE
        } else {
            constraintLayoutBottomCard.visibility = View.INVISIBLE
            toolbar_activity_tracker.visibility = View.INVISIBLE
        }
    }

    override fun onAnimationStart(p0: Animation?) {
        if (constraintLayoutBottomCard.visibility == View.INVISIBLE) {
//            constraintLayoutBottomCard.visibility = View.VISIBLE
//            toolbar_activity_tracker.visibility = View.VISIBLE
        }
    }
}
