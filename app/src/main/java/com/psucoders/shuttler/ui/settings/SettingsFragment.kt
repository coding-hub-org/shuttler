package com.psucoders.shuttler.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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

        loadSpinner()
        listenForEvents()

        return view
    }

    private fun loadSpinner() {
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

        settingsViewModel._timeAhead.observe(this, Observer { time ->
            timeAheadMinutes.text = time
        })

        cbEnableNotifications.setOnCheckedChangeListener { _, isChecked ->
            Toast.makeText(activity, isChecked.toString(), Toast.LENGTH_SHORT).show()
        }

        buttonMinus.setOnClickListener {
            settingsViewModel.decreaseTimeCounter(timeAheadMinutes.text.toString().toInt())
        }

        buttonPlus.setOnClickListener {
            settingsViewModel.increaseTimeCounter(timeAheadMinutes.text.toString().toInt())
        }

        buttonLogout.setOnClickListener {
            settingsViewModel.logoutStatus.observe(this, Observer { status ->
                if (status) {
                    startActivity(Intent(activity, LoginActivity::class.java))
                }
            })
            settingsViewModel.logout()

        }
    }
}
