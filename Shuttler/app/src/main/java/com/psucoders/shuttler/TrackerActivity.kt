package com.psucoders.shuttler

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.RelativeLayout
import android.widget.Toast
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.psucoders.shuttler.Model.NotifyUser
import io.vrinda.kotlinpermissions.PermissionCallBack
import io.vrinda.kotlinpermissions.PermissionsActivity
import kotlinx.android.synthetic.main.activity_tracker.*
import org.jetbrains.anko.toast
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TrackerActivity : PermissionsActivity(), OnMapReadyCallback, Animation.AnimationListener {

    private var NOTIFY_USERS = "http://new.beginurrev.com/Shuttler/send_notification_form.php"
    // Users location variables
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CODE = 2310

    // Driver database
    private lateinit var drivers: DatabaseReference

    private lateinit var mMap: GoogleMap
    private lateinit var mapView: View
    private var shuttleMarker: Marker? = null
    private lateinit var mAuth: FirebaseAuth
    var tokenId = ""
    private lateinit var geoFire: GeoFire

    //private lateinit var shuttleMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)
        checkLocationPermission()
        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        manageTokens()

        val toolbar: Toolbar = findViewById(R.id.toolbar_activity_tracker)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Shuttle Status"

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        mapView = mapFragment.view!!
        drivers = FirebaseDatabase.getInstance().getReference("Drivers")
        geoFire = GeoFire(drivers)
        displayLocation()
    }

    override fun onStart() {
        super.onStart()
        // TODO: Check if shuttle is working
        buildLocationRequest()
        buildLocationCallBack()
        // Create FusedLocationProvider
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }


    private fun displayLocation() {
        // Read from the database
        val driverListener = object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    //val message = dataSnapshot.getValue(Message::class.java)
                    val latDriver = dataSnapshot.children.elementAt(0).child("l").child("0").value
                    val longDriver = dataSnapshot.children.elementAt(0).child("l").child("1").value

                    if (shuttleMarker != null) {
                        shuttleMarker!!.remove()
                    }

                    shuttleMarker = mMap.addMarker(MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_shuttle))
                            .position(LatLng(latDriver as Double, longDriver as Double))
                            .title("Shuttle"))

                    shuttlerFAB!!.setOnClickListener {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(latDriver, longDriver), 15f))
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Failed to read value
            }
        }

        drivers.addValueEventListener(driverListener)
    }

    private fun manageTokens() {
        tokenId = MyFirebaseMessagingService.getToken(applicationContext)
        Log.d("Token is", tokenId)
        val database = FirebaseDatabase.getInstance()
        val username = getUsername()
        val myRef = database.getReference("Users").child(username).child("notifications")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val post = dataSnapshot.getValue(UserSettingValues::class.java)
                val tokens = post!!.tokens
                var isChecked = true
                var oldToken = ""

                try {
                    isChecked = tokens[tokens.keys.elementAt(0)]!!
                    myRef.child("tokens").child(MyFirebaseMessagingService.getToken(applicationContext)).setValue(isChecked)
                    Log.d("is", "checked - $isChecked")
                    oldToken = tokens.keys.elementAt(0)
                    Log.d("Token old", oldToken)
                    if (tokenId == oldToken) {
                        Log.d("Same tokenss", "happily")
                    } else {
                        Log.d("Different tokenss", "sadly")
                        myRef.child("tokens").removeValue()
                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
                myRef.child("tokens").child(tokenId).setValue(isChecked)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("ERRROR", "reading db")
            }
        })
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // TODO: Add / remove map style
        /*try {
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
        }*/

        mMap.isIndoorEnabled = true
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(44.690798, -73.467171), 12f))

        mMap.setOnMapClickListener {
            animations()
        }

//        updateCamera(20f)
    }

    fun updateCamera(bearing: Float) {
        val currentPlace = CameraPosition.Builder()
                .target(LatLng(44.6934, -73.4860))
                .bearing(bearing).tilt(65.5f).build()
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentPlace))
    }


    /*NOTIFY USERS*/
	/*This method is not complete yet*/
    fun notifyUsers(location: String) {
        Log.d("entered", "geofence of $location")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {


                val users = dataSnapshot.children

                for(child in users){
                    child.getValue(NotifyUser::class.java)
                    val test = child.value as NotifyUser
                    Log.d("Life is", "Hard" + test.email)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("ERRROR", "reading db")
            }
        })

    }

    fun collectNotificationData(value: HashMap<String, Any>) {
        Log.d("values here", value.toString())
        var userList = value.keys
        Log.d("values here", userList.size.toString())
    }

    fun extractDataFromSnapshot(temp: HashMap<Int, Any>){
        var user = ArrayList<String>()
        var notifications = HashMap<String, Any>()

        for(i in 0 until temp.size){
            Log.d("here i am", "" + temp[i])
        }
    }




    /**
     * Ask for location permission access
     * @return void
     */
    private fun checkLocationPermission() {
        requestPermissions(Manifest.permission.ACCESS_FINE_LOCATION, object : PermissionCallBack {
            override fun permissionGranted() {
                super.permissionGranted()
                mMap.isMyLocationEnabled = true



                // Move location of my location button
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
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            checkLocationPermission()
                        }
                        .setNegativeButton(android.R.string.no) { _, _ ->
                            finish()
                            applicationContext.toast("Okay")
                        }
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show()
            }
        })
    }


    /**
     * Animate the layout when user taps on map
     * @param
     * @return void.
     */
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

    // Animation helper functions
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

    /**
     * Inflate the menu; this adds items to the action bar if it is present.
     * @param menu Toolbar menu
     * @return true if menu is inflated false otherwise.
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.maps_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        val intent = Intent(this@TrackerActivity, SettingsActivity::class.java)
        startActivity(intent)
        // TODO: Delete toast for deployment
        baseContext.toast("clicked settings")
        return super.onOptionsItemSelected(item)
    }

    // User's current position
    private fun buildLocationCallBack() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                // Get last location
                val location = p0!!.locations[p0.locations.size - 1]
                fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                val yourPosition = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourPosition, 14f))
            }
        }
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 2000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun getUsername(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }
}
