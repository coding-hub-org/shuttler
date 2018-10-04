package com.psucoders.shuttler

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_settings.*
import android.support.v4.app.NavUtils
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SettingsActivity : AppCompatActivity() {
    lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val mAuth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar_activity_settings)
        setSupportActionBar(toolbar)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        supportActionBar!!.title = "Settings"

// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.locations_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            locationsSpinner.adapter = adapter
        }

        buttonMinus.setOnClickListener {
            mins.text = (Integer.parseInt(mins.text.toString()) - 1).toString()
        }

        buttonPlus.setOnClickListener {
            mins.text = (Integer.parseInt(mins.text.toString()) + 1).toString()
        }

        button_apply.setOnClickListener {
            val locationForNotification = locationsSpinner.selectedItem.toString()
            val timeAhead = mins.text.toString()
            val enableNotifications = notification_enabled.isChecked

            val database = FirebaseDatabase.getInstance()
            val myRef = database.getReference("greye003")
            myRef.child("notifications").child("enabled").setValue(enableNotifications.toString())
            myRef.child("notifications").child("notifyLocation").setValue(locationForNotification)
            myRef.child("notifications").child("timeAhead").setValue(timeAhead)
        }

        button_logout.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

    }

    private fun getValues() {

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
