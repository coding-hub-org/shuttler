package com.psucoders.geolocationandroid

import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnGeofences.setOnClickListener {
            val i = Intent(this, GeolocationActivity::class.java)
            startActivity(i)
        }

    }

}
