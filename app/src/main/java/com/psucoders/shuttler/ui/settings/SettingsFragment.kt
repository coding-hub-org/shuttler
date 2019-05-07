package com.psucoders.shuttler.ui.settings

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.login.LoginActivity


class SettingsFragment : Fragment() {

    private lateinit var locationsSpinner: Spinner
    private lateinit var cbEnableNotifications: CheckBox

    private lateinit var buttonPlus: Button
    private lateinit var buttonMinus: Button
    private lateinit var buttonLogout: Button

    private lateinit var timeAheadMinutes: TextView

    companion object {
        fun newInstance() = SettingsFragment()
    }

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        val view = inflater.inflate(R.layout.settings_fragment, container, false)

        locationsSpinner = view.findViewById(R.id.locationsSpinner)
        cbEnableNotifications = view.findViewById(R.id.checkbox_enable_notifications)
        timeAheadMinutes = view.findViewById(R.id.time_ahead_minutes)
        buttonMinus = view.findViewById(R.id.button_minus)
        buttonPlus = view.findViewById(R.id.button_plus)
        buttonLogout = view.findViewById(R.id.button_logout)

        loadDefaults()
        fetchCurrentSettingsFromFirebase()
        getFcmToken()
        listenForEvents()

        locationsSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                // your code here
                Toast.makeText(activity, "Selected: " + locationsSpinner.selectedItem, Toast.LENGTH_SHORT).show()
                settingsViewModel.updateNotificationLocation(locationsSpinner.selectedItem.toString())
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                // your code here
            }

        }

        return view
    }

    private fun fetchCurrentSettingsFromFirebase() {
        settingsViewModel.getExistingSettings.observe(this, Observer { settings ->
            settingsViewModel.timeAhead.value = settings.timeAhead
            val myAdap = locationsSpinner.adapter as ArrayAdapter<String> //cast to an ArrayAdapter
            val spinnerPosition = myAdap.getPosition(settings.notifyLocation)
            val enabled = settings.tokens!![settings.tokens.keys.iterator().next()]
            cbEnableNotifications.isChecked = enabled!!
//set the default according to value
            locationsSpinner.setSelection(spinnerPosition)
        })
        settingsViewModel.fetchCurrentSettingsFromFirebase()
    }

    private fun loadDefaults() {
        ArrayAdapter.createFromResource(
                context!!,
                R.array.locations_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            locationsSpinner.adapter = adapter
        }
        settingsViewModel.notificationsEnabled.value = cbEnableNotifications.isChecked
        settingsViewModel.timeAhead.value = timeAheadMinutes.text.toString()
    }

    private fun listenForEvents() {

        settingsViewModel.timeAhead.observe(this, Observer { time ->
            timeAheadMinutes.text = time
        })

        cbEnableNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(activity, isChecked.toString(), Toast.LENGTH_SHORT).show()
            settingsViewModel.updateNotificationEnabled(isChecked)
        }

        buttonMinus.setOnClickListener {
            settingsViewModel.decreaseTimeCounter(timeAheadMinutes.text.toString().toInt())
        }

        buttonPlus.setOnClickListener {
            settingsViewModel.increaseTimeCounter(timeAheadMinutes.text.toString().toInt())
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

    private fun getFcmToken() {
        val token = activity!!.getSharedPreferences("_", MODE_PRIVATE).getString("fb", "empty")
        settingsViewModel.currentToken.value = token
    }
}
