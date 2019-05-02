package com.psucoders.shuttler

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.psucoders.shuttler.Model.NotifyUser
import kotlinx.android.synthetic.main.activity_driver.*
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class DriverActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val mAuth = FirebaseAuth.getInstance()!!
    private lateinit var mapFragment: SupportMapFragment

    // Access firestore
    private lateinit var drivers: DatabaseReference
    private lateinit var geoFire: GeoFire
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private val REQUEST_CODE = 2310
    private val jsonParser = JSONParser()


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        Toast.makeText(this@DriverActivity, "PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                    else
                        Toast.makeText(this@DriverActivity, "NOTs PERMISSION GRANTED", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun logoutDriver(v: View) {
        // Logout driver
        mAuth.signOut()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        // GeoFire
        drivers = FirebaseDatabase.getInstance().getReference("Drivers")
        geoFire = GeoFire(drivers)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapDriver) as SupportMapFragment
        mapFragment.getMapAsync(this)

        notifyButton.setOnClickListener {
            notifyUsers("Market32")
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
        supportFragmentManager.beginTransaction().hide(mapFragment).commit()

        Toast.makeText(this, "MAP READY", Toast.LENGTH_LONG).show()
        val campus = LatLng(44.692884, -73.465875) // Done
        val walmart = LatLng(44.692800, -73.486811) // Done
        val priceChopper = LatLng(44.695457, -73.492659) // Done
        val target = LatLng(44.703143, -73.492592) // Done
        val jade = LatLng(44.698763, -73.476522) // Done

        mMap.addCircle(CircleOptions()
                .center(campus)
                .radius(50.0)
                .strokeColor(0X220000FF)
                .fillColor(0X220000FF)
                .strokeWidth(3.0f)
        )
        // Check permissions
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION))
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        else {
            buildLocationRequest()
            buildLocationCallback()
            // Create FusedProviderClient
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

            // Set event
            switchDuty.setOnCheckedChangeListener { _, checked ->
                if (checked) {
                    mMap.isMyLocationEnabled = true
                    toast("ON DUTY")
                    if (ActivityCompat.checkSelfPermission(this@DriverActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this@DriverActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@DriverActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
                        return@setOnCheckedChangeListener
                    }

                    fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())

                    geoFence(campus, "Campus")
                    geoFence(walmart, "walmart")
                    geoFence(priceChopper, "Market32")
                    geoFence(target, "Target")
                    geoFence(jade, "Jade")

                } else {
                    toast("OFF DUTY")
                    if (ActivityCompat.checkSelfPermission(this@DriverActivity, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(this@DriverActivity, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@DriverActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), REQUEST_CODE)
                        return@setOnCheckedChangeListener
                    }

                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                }
            }
        }
    }

    private fun geoFence(locat: LatLng, locatName: String) {
        val geoQuery = geoFire.queryAtLocation(GeoLocation(locat.latitude, locat.longitude), 0.05)
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            // user has been found within the radius:
            override fun onKeyEntered(key: String, location: GeoLocation) {
                Toast.makeText(this@DriverActivity, "ENTER GEOFENCE", Toast.LENGTH_LONG).show()

                //Work with notifications HERE!
                notifyUsers(locatName)

            }

            override fun onKeyExited(key: String) {
                Toast.makeText(this@DriverActivity, "EXITING GEOFENCE", Toast.LENGTH_LONG).show()
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {

            }

            // all users within the radius have been identified:
            override fun onGeoQueryReady() {
            }

            override fun onGeoQueryError(error: DatabaseError) {

            }
        })
    }

    private fun notifyUsers(key: String) {
        val url = "http://new.beginurrev.com/Shuttler/send_notification_form.php"
        fetchDataFromFirebase(url, key)
    }

    private fun fetchDataFromFirebase(url: String, locationName: String) {

        var tokens = ""
        /*NOTIFY USERS*/
        /*This method is not complete yet*/
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users/")


        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val users = dataSnapshot.children

                doAsync {
                    for (child in users) {
                        val temp = child.getValue(NotifyUser::class.java)
                        val notifs = temp!!.notifications
                        if (notifs.notifyLocation == locationName) {
                            val test = notifs.tokens
                            for (a: String in test.keys) {
                                if (test[a] == true) {
                                    tokens = "$tokens$a "
                                }
                            }
                        }
                    }

                    uiThread {
                        doAsync {
                            try {
                                Log.d("HERE IS TOKENS", tokens)
                                //building parameters
                                val params = ArrayList<NameValuePair>()
                                params.add(BasicNameValuePair("keys", tokens))
                                params.add(BasicNameValuePair("title", "SHUTTLER"))
                                params.add(BasicNameValuePair("message", "The shuttle is arriving soon!"))
                                jsonParser.makeHttpRequest(url, "POST", params)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            uiThread {
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("ERRROR", "reading db")
            }
        })
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                // Get last location
                val location = p0!!.locations[p0.locations.size - 1]
                Toast.makeText(this@DriverActivity, "LATITUDE: ${location.latitude}  LONGITUDE: ${location.longitude}", Toast.LENGTH_LONG).show()
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
}
