package com.psucoders.shuttler.ui.email

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import com.psucoders.shuttler.R
import kotlinx.android.synthetic.main.activity_email.*


class EmailActivity : AppCompatActivity() {
    val context = this;
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

        // Firebase auth
        val actionCodeSettings = ActionCodeSettings.newBuilder()
                // URL you want to redirect back to. The domain (www.example.com) for this
                // URL must be whitelisted in the Firebase Console.
                .setUrl("https://shuttler.page.link/androidAuth")
                // This must be true
                .setHandleCodeInApp(true)
                .setAndroidPackageName(
                        "com.psucoders.shuttler",
                        true, /* installIfNotAvailable */
                        "12" /* minimumVersion */)
                .build()


        // Get email from editText
        val email = editText.text
        val auth = FirebaseAuth.getInstance()

        // TODO: Check input email
        floatingActionButton.setOnClickListener {
            auth.sendSignInLinkToEmail(email.toString(), actionCodeSettings)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Email sent. to $email", Toast.LENGTH_LONG).show()
                        }
                    }
//            Toast.makeText(context, "Email sent. to ${email.toString()}", Toast.LENGTH_LONG).show()

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
