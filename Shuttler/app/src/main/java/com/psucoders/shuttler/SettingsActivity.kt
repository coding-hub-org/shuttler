package com.psucoders.shuttler

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.NavUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_settings.*
import org.apache.http.NameValuePair
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class SettingsActivity : AppCompatActivity() {
    lateinit var queue: RequestQueue
    private val jsonParser = JSONParser()
    private lateinit var username: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        username = getUsername()
        val mAuth = FirebaseAuth.getInstance()
        val toolbar: Toolbar = findViewById(R.id.toolbar_activity_settings)
        setSupportActionBar(toolbar)
        queue = Volley.newRequestQueue(this)

        fetchDataFromFirebase()

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
            val myRef = database.getReference("Users").child(username).child("notifications")
            Log.d("token is here", "" + MyFirebaseMessagingService.getToken(applicationContext))
            myRef.child("tokens").child(MyFirebaseMessagingService.getToken(applicationContext)).setValue(enableNotifications)
            myRef.child("notifyLocation").setValue(locationForNotification)
            myRef.child("timeAhead").setValue(timeAhead)
        }

        button_logout.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        button_test.setOnClickListener {
            sendNotif()
        }
    }

    private fun sendNotif() {
        val url = "http://new.beginurrev.com/Shuttler/send_notification_form.php"

        doAsync {
            try {
                //building parameters
                val params = ArrayList<NameValuePair>()
                params.add(BasicNameValuePair("token", "eFvusFj3vzk:APA91bHbFjibqGG29Cs-9VBCkf9LR7R7JQOcxlIC0ehrtU3j4qKu-adCqGFBrp4jrYM4BXpF7Vez7cMvYUbdpllsegdyDN6HYXY4nYHCS2LE6RVb2fenrKCa85c_filK6ku5KFCKDSgV"))
                params.add(BasicNameValuePair("title", "TEST"))
                params.add(BasicNameValuePair("message", "MESSAGE HERE"))
                jsonParser.makeHttpRequest(url, "POST", params)

            } catch (e: Exception) {
                e.printStackTrace()
            }

            uiThread {

            }

        }
    }

    private fun fetchDataFromFirebase() {
        Log.d("reached", "here1$username")

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("Users").child(username).child("notifications")
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("reached", "here")
                val post = dataSnapshot.getValue(UserSettingValues::class.java)
                Log.d("values are1", post!!.timeAhead)
                Log.d("values are2", post.notifyLocation)

                val testArr = resources.getStringArray(R.array.locations_array)
                val testArrList = testArr.toList()
                val index = testArrList.indexOf(post.notifyLocation)
                val tokens = post.tokens

                val isChecked = tokens[tokens.keys.elementAt(0)]

                Log.d("poooo", index.toString() + " " + isChecked)

                locationsSpinner.setSelection(index)

                notification_enabled.isChecked = isChecked!!
                mins.text = post.timeAhead
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("ERRROR", "reading db")
            }
        })
    }

    private fun getValues() {

    }

    private fun getUsername(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
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
