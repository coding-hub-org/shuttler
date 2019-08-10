package com.psucoders.shuttler.ui.notifications

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.psucoders.shuttler.R
import com.psucoders.shuttler.ui.dashboard.DashboardActivity
import com.psucoders.shuttler.utils.adapters.NotificationAdapter


class NotificationsFragment : Fragment() {

    private val adapter = NotificationAdapter()

    private lateinit var noNotificationsLayout: RelativeLayout
    private lateinit var recyclerView: RecyclerView

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.notifications_fragment, container, false)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(NotificationsViewModel::class.java)

        recyclerView = view.findViewById(R.id.notification_recycler_view)
        noNotificationsLayout = view.findViewById(R.id.noNotificationLayout)

        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter
        recyclerView.visibility = View.GONE
        noNotificationsLayout.visibility = View.VISIBLE

        (activity as DashboardActivity).supportActionBar?.title = getString(R.string.notifications_fragment)

        fetchData()
        return view
    }

    private fun fetchData() {
        viewModel.fetchNotifications()
        viewModel.allNotifications.observe(this, Observer { notifications ->
            Log.d("LOG LIFE", "size: " + notifications.size)
            if (notifications.size == 0) {
                recyclerView.visibility = View.GONE
                noNotificationsLayout.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                noNotificationsLayout.visibility = View.GONE
                adapter.setNotifications(notifications)
            }
        })

    }
}
