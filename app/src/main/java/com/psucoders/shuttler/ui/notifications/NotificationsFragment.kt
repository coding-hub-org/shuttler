package com.psucoders.shuttler.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.model.NotificationFragmentModel
import com.psucoders.shuttler.utils.adapters.NotificationAdapter


class NotificationsFragment : Fragment() {

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    private lateinit var viewModel: NotificationsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.notifications_fragment, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.notification_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        val adapter = NotificationAdapter()
        recyclerView.adapter = adapter

        val testList = ArrayList<NotificationFragmentModel>()
        testList.add(NotificationFragmentModel("Shuttler Life", "No wife", "May 8"))
        testList.add(NotificationFragmentModel("Shuttler Notif Title", "LOLLLL", "May 6"))
        adapter.setNotifications(testList)

        return view
    }



    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}
