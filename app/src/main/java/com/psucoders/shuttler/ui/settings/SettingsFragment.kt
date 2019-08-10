package com.psucoders.shuttler.ui.settings

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.dashboard.DashboardActivity
import com.psucoders.shuttler.ui.login.LoginActivity


class SettingsFragment : Fragment() {

    private lateinit var locationsSpinner: Spinner
    private lateinit var cbEnableNotifications: Switch
    private lateinit var buttonLogout: Button
    private lateinit var codingHubText: TextView
    private lateinit var currentUserEmail: TextView

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        settingsViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(SettingsViewModel::class.java)

        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        locationsSpinner = view.findViewById(R.id.locationsSpinner)
        cbEnableNotifications = view.findViewById(R.id.switchEnableNotifications)
        buttonLogout = view.findViewById(R.id.button_logout)
        codingHubText = view.findViewById(R.id.codingHubText)
        currentUserEmail = view.findViewById(R.id.loggedInAs)

        (activity as DashboardActivity).supportActionBar?.title = getString(R.string.settings_fragment)

        loadSpinnerData()
        fetchCurrentSettingsFromSharedPreferences()
        listenForEvents()

        locationsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                // your code here
                Toast.makeText(activity, "Selected: " + locationsSpinner.selectedItem, Toast.LENGTH_SHORT).show()
                settingsViewModel.updateNotificationLocation(locationsSpinner.selectedItem.toString())
                activity!!.getSharedPreferences("_", MODE_PRIVATE).edit().putString("notifyLocation", locationsSpinner.selectedItem.toString()).apply()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }
        }
        return view
    }

    private fun fetchCurrentSettingsFromSharedPreferences() {
        val sharedPreferences = activity!!.getSharedPreferences("_", MODE_PRIVATE)
        val myAdapter = locationsSpinner.adapter as ArrayAdapter<String>
        val spinnerPosition = myAdapter.getPosition(sharedPreferences.getString("notifyLocation", "Walmart"))
        val enabledNotifications = sharedPreferences.getBoolean("notificationsEnabled", true)

        settingsViewModel.currentToken.value = getFcmToken()
        settingsViewModel.notificationsEnabled.value = cbEnableNotifications.isChecked
        locationsSpinner.setSelection(spinnerPosition)
        cbEnableNotifications.isChecked = enabledNotifications
    }

    private fun loadSpinnerData() {
        codingHubText.movementMethod = LinkMovementMethod.getInstance()

        settingsViewModel.getCurrentUserEmail.observe(this, Observer { email ->
            currentUserEmail.text = email
        })
        settingsViewModel.fetchCurrentUser()

        ArrayAdapter.createFromResource(
                context!!,
                R.array.locations_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationsSpinner.adapter = adapter
        }
    }

    private fun listenForEvents() {

        cbEnableNotifications.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked)
                Toast.makeText(activity, "Notifications enabled", Toast.LENGTH_SHORT).show()
            else
                Toast.makeText(activity, "Notifications disabled", Toast.LENGTH_SHORT).show()

            settingsViewModel.updateNotificationEnabled(isChecked)
            activity!!.getSharedPreferences("_", MODE_PRIVATE).edit().putBoolean("notificationsEnabled", cbEnableNotifications.isChecked).apply()
        }

        buttonLogout.setOnClickListener {
            settingsViewModel.getLogoutStatus.observe(this, Observer { status ->
                if (status) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            })
            settingsViewModel.logout()

        }

    }

    private fun getFcmToken(): String? {
        return activity!!.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
    }
}
