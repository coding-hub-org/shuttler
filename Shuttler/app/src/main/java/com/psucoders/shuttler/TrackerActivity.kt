package com.psucoders.shuttler

import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import kotlinx.android.synthetic.main.activity_tracker.*
import org.jetbrains.anko.toast

class TrackerActivity : PermissionsActivity(), OnMapReadyCallback, Animation.AnimationListener {

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        checkLocationPermission()

        val toolbar: Toolbar = findViewById(R.id.toolbar_activity_tracker)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Shuttle Status"

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.view!!
    }

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

        mMap.setOnMapClickListener {
            animations()
        }
    }

    /*
    * EVERYTHING TO DO WITH PERMISSIONS
    * If granted, gets location. Else displays message to ask again. If not granted, app exits
    * */
    private fun checkLocationPermission() {
        requestPermissions(Manifest.permission.ACCESS_COARSE_LOCATION, object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                mMap.isMyLocationEnabled = true


                /*Move location of my location button*/
                if (mapView.findViewById<View>(Integer.parseInt("1")) != null) {
                    // Get the button view
                    val locationButton = (mapView.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))
                    // and next place it, on bottom right (as Google Maps app)
                    val layoutParams = locationButton.layoutParams as RelativeLayout.LayoutParams
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE)
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE)
                    layoutParams.setMargins(0, toolbar_activity_tracker.height + 20, 20, 0)
                }
            }

            override fun permissionDenied() {
                super.permissionDenied()
                val builder: AlertDialog.Builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AlertDialog.Builder(this@TrackerActivity, android.R.style.Theme_Material_Dialog_Alert)
                } else {
                    AlertDialog.Builder(this@TrackerActivity)
                }
                builder.setTitle("Permissions Required")
                        .setMessage("We require permissions to provide you with the best experience of the app")
                        .setPositiveButton(android.R.string.ok) { dialog, which ->
                            checkLocationPermission()
                        }
                        .setNegativeButton(android.R.string.no) { dialog, which ->
                            finish()
                            applicationContext.toast("Okay")
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
            }
        })
    }

    /*
    * Animations for the toolbar and the bottom layout
    * */
    private fun animations() {
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
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //         Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.maps_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this@TrackerActivity, SettingsActivity::class.java)
        startActivity(intent)
        baseContext.toast("clicked settings")
        return super.onOptionsItemSelected(item)
    }

}
