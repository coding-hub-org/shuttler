package com.psucoders.shuttler.ui.driver

import android.content.Intent
import androidx.core.app.NotificationCompat
import com.psucoders.shuttler.utils.App.Companion.CHANNEL_ID
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import android.location.Location
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import android.util.Log
import android.app.*
import android.content.pm.PackageManager
import android.os.*
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.firebase.geofire.GeoQueryEventListener
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.model.NotificationSentModel
import com.psucoders.shuttler.utils.notifications.NotificationService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class DriverService : Service() {

    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var mCurrentLocation: Location
    private lateinit var db: FirebaseFirestore
    private lateinit var driver: String
    private lateinit var geoFire: GeoFire

    override fun onCreate() {
        super.onCreate()
        db = FirebaseFirestore.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(applicationContext)
        buildLocationRequest()
        buildLocationCallback()
        // get user id here
        driver = "cdKOppgDPxGjj0roFfUG"
        db.collection("drivers").document(driver).update("active", true)
        val drivers = FirebaseDatabase.getInstance().getReference("Drivers")
        geoFire = GeoFire(drivers)
        geoFence(LatLng(44.692800, -73.486811), "Walmart")
        geoFence(LatLng(44.703143, -73.492592), "Target")
        geoFence(LatLng(44.695457, -73.492659), "Market32")
        geoFence(LatLng(44.698763, -73.476522), "Jade")
        geoFence(LatLng(44.692884, -73.465875), "Campus")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val message = "Tracking Shuttle location"

        val notificationIntent = Intent(this, DriverActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Driver Service")
                .setContentText(message)
                .setSmallIcon(R.drawable.shuttler_logo)
                .setContentIntent(pendingIntent)
                .build()
        startForeground(1, notification)
        if (ContextCompat.checkSelfPermission
                (applicationContext, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient
                    .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
        return START_NOT_STICKY
    }

    private fun geoFence(location: LatLng, locationName: String) {
        val geoQuery = geoFire.queryAtLocation(GeoLocation(location.latitude, location.longitude), 0.5)
        geoQuery.addGeoQueryEventListener(object : GeoQueryEventListener {
            // user has been found within the radius:
            override fun onKeyEntered(key: String, location: GeoLocation) {

                Toast.makeText(applicationContext, "KEY -> $key LOCATION -> $location" +
                        " ", Toast.LENGTH_LONG).show()

                val retrofit = Retrofit.Builder().baseUrl("https://us-central1-shuttler-p001.cloudfunctions.net/").addConverterFactory(GsonConverterFactory.create()).build()
                val notificationService = retrofit.create(NotificationService::class.java)
                val call = notificationService.sendNotification(locationName)


                call.enqueue(object : Callback<NotificationSentModel> {
                    override fun onResponse(call: Call<NotificationSentModel>, response: retrofit2.Response<NotificationSentModel>) {
                        Log.d("test", "test$response")
                    }

                    override fun onFailure(call: Call<NotificationSentModel>?, t: Throwable?) {
                        // failure
                        Log.d("test", "test${t.toString()}")
                    }
                })
                //Work with notifications HERE!
                //notifyUsers(locatName)
            }

            override fun onKeyExited(key: String) {
                Toast.makeText(applicationContext, "EXITING GEOFENCE", Toast.LENGTH_LONG).show()
            }

            override fun onKeyMoved(key: String, location: GeoLocation) {

            }

            // all users within the radius have been identified:
            override fun onGeoQueryReady() {
            }

            override fun onGeoQueryError(error: DatabaseError) {
                Toast.makeText(applicationContext, "DB ERROR", Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        db.collection("drivers").document("cdKOppgDPxGjj0roFfUG").update("active", false)
        Toast.makeText(applicationContext, "destroyed", Toast.LENGTH_LONG).show()
    }

    override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 3000
        locationRequest.smallestDisplacement = 10f
    }

    private fun buildLocationCallback() {
        Log.d("BUILD LOCATION CALLBACK", "LOCATION CALLED")
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                super.onLocationResult(locationResult)
                mCurrentLocation = locationResult!!.lastLocation
                val geoPoint = GeoPoint(mCurrentLocation.latitude, mCurrentLocation.longitude)
                db.collection("drivers").document(driver).update("location", geoPoint)
                Toast.makeText(applicationContext, "LATITUDE: ${mCurrentLocation.latitude}  LONGITUDE: ${mCurrentLocation.longitude}", Toast.LENGTH_LONG).show()
                geoFire.setLocation(driver, GeoLocation(mCurrentLocation.latitude, mCurrentLocation.longitude)) { _, _ ->
                    return@setLocation
                }
            }
        }
    }
}


