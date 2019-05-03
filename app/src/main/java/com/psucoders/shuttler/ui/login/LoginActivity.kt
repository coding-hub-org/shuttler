package com.psucoders.shuttler.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.firebase.FirebaseSingleton
import kotlinx.android.synthetic.main.login_activity.*
import org.jetbrains.anko.toast

class LoginActivity : AppCompatActivity() {

    private val loginViewModel = LoginViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginViewModel.checkIfUserExists()

        setContentView(R.layout.login_activity)

        logout_test.setOnClickListener {
            toast("CLICKED")
            val firebaseObj = FirebaseSingleton.getInstance()
            firebaseObj.logOut()
        }
    }

}