package com.codinghub.shuttler.mobile.ui.driverLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.codinghub.shuttler.mobile.R
import com.codinghub.shuttler.mobile.ui.driver.DriverActivity
import com.codinghub.shuttler.mobile.ui.login.LoginViewModel
import kotlinx.android.synthetic.main.activity_driver_login.*

class DriverLoginActivity : AppCompatActivity() {

    private lateinit var driverLoginViewModel: DriverLoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_login)
        driverLoginViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(DriverLoginViewModel::class.java)
        observeExistingUser()
    }

    private fun observeExistingUser() {
        driverLoginViewModel.userLoggedIn.observe(this, Observer { userExists ->
            if (userExists) {
                driverLoginViewModel.checkIfUserIsDriver()
                driverLoginViewModel.isDriver.observe(this, Observer { isDriver ->
                    if (isDriver) {
                        startActivity(Intent(this, DriverActivity::class.java))
                    }
                })
                finish()
            }
        })
        driverLoginViewModel.checkIfUserExists()
    }


    fun handleLogin(v: View) {
        btnSignIn.isEnabled = false
        driverLoginViewModel.loginUser(edtUser.text.toString(), edtPassword.text.toString())
        driverLoginViewModel.validFields.observe(this, Observer { valid ->
            if (valid != null && !valid) {
                Snackbar.make(loginDriverRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
                driverLoginViewModel.resetValidity()
            }
            if (valid != null && valid) {
                driverLoginViewModel.checkIfUserExists()
            }
            btnSignIn.isEnabled = true
        })
//        Snackbar.make(loginDriverRoot, "Invalid credentials. Please check your username / password", Snackbar.LENGTH_LONG).show()
    }
}
