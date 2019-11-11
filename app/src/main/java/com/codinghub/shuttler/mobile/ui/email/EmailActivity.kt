package com.codinghub.shuttler.mobile.ui.email

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.codinghub.shuttler.mobile.R
import com.codinghub.shuttler.mobile.ui.authentication.AuthenticationActivity
import kotlinx.android.synthetic.main.activity_email.*


class EmailActivity : AppCompatActivity() {
    companion object {
        const val preferences = "shuttlerPreferences"
        const val emailKey = "emailKey"
        const val isSignedInKey = "isSignedIn"
    }

    lateinit var sharedPreferences: SharedPreferences
    val context = this
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        // Display keyboard by default
        // TODO: Fix display keyboard
        val editText = findViewById<EditText>(R.id.emailEditText)
        editText.requestFocus()
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences(preferences, Context.MODE_PRIVATE)

        val actionCodeSettings = buildActionCodeSettings()

        // Get email from editText
        val email = editText.text

        // TODO: Check input email
        floatingActionButton.setOnClickListener {
            if (validEmail(email.toString()))
                sendSignInLink(email.toString(), actionCodeSettings)
            else {
                Snackbar.make(emailConstraintLayout, "Invalid Email", Snackbar.LENGTH_LONG)
                        .setAction("More", View.OnClickListener {
                            // build alert dialog
                            val dialogBuilder = AlertDialog.Builder(this)

                            val msg = "Seems like you've entered an invalid email. Check that you used a valid Plattsburgh email and" +
                                    " that the input field is not empty"
                            // set message of alert dialog
                            dialogBuilder.setMessage(msg)
                                    // if the dialog is cancelable
                                    .setCancelable(false)
                                    // positive button text and action
                                    .setPositiveButton("Got it", DialogInterface.OnClickListener {
                                        dialog, id -> finish()
                                    })

                            // create dialog box
                            val alert = dialogBuilder.create()
                            // set title for alert dialog box
                            alert.setTitle("AlertDialogExample")
                            // show alert dialog
                            alert.show()
                        }).show()
            }
        }
    }

    private fun validEmail(email: String): Boolean {
        if (email.isNotEmpty() && email.contains("@plattsburgh.edu")) {
            return true
        }
        return false
    }

    private fun buildActionCodeSettings(): ActionCodeSettings {
       return ActionCodeSettings.newBuilder()
               .setUrl("https://shuttler.page.link/androidAuth")
               .setHandleCodeInApp(true)
               .setAndroidPackageName(
                       "com.codinghub.shuttler.mobile",
                       true,
                       "12" /* minimumVersion */)
               .build()
    }

    private fun sendSignInLink(email: String, actionCodeSettings: ActionCodeSettings) {
        val auth = FirebaseAuth.getInstance()
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Email sent. to $email", Toast.LENGTH_LONG).show()
                    val editor = sharedPreferences.edit()
                    editor.putString(emailKey, email)
                    editor.apply()
                    startActivity(Intent(this, AuthenticationActivity::class.java))
                }
            }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            if (item.itemId == android.R.id.home) {
                super.onBackPressed()
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}
