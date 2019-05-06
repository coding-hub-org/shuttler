package com.psucoders.shuttler

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_driver.*
import org.jetbrains.anko.toast

class DriverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        // Set event
        switchDuty.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                toast("ON DUTY")
                startService()
            } else {
                toast("OFF DUTY")
                stopService()
            }
        }
    }

    private fun startService() {
        val serviceIntent = Intent(this, DriverService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, DriverService::class.java)
        stopService(serviceIntent)
    }
}
