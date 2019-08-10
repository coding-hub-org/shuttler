package com.psucoders.shuttler.ui.driver

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.login.LoginActivity
import com.psucoders.shuttler.utils.helpers.PermissionRequester
import kotlinx.android.synthetic.main.activity_driver.*
import org.jetbrains.anko.toast

class DriverActivity : AppCompatActivity() {

    companion object {
        const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2310
    }

    private lateinit var driversViewModel: DriverActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)
        driversViewModel = ViewModelProvider(this,
                ViewModelProvider.NewInstanceFactory()).get(DriverActivityViewModel::class.java)

        btnLogoutDriver.setOnClickListener {
            stopService()
            driversViewModel.getLogoutStatus.observe(this, Observer { loggedOut ->
                if (loggedOut) startActivity(Intent(this, LoginActivity::class.java))
                finish()
            })
            driversViewModel.logout()
        }

        switchDuty.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                if (ActivityCompat.checkSelfPermission(this@DriverActivity,
                                android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    toast("ON DUTY")
                    startService()
                } else {
                    PermissionRequester().requestLocationPermission(this@DriverActivity)
                }
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
