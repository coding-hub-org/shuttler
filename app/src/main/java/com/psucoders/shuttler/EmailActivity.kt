package com.psucoders.shuttler

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.psucoders.shuttler.ui.login.LoginActivity


class EmailActivity : AppCompatActivity() {
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
