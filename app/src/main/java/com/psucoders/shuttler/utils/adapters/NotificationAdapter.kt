package com.psucoders.shuttler.utils.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.model.NotificationFragmentModel

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {

    var notificationList = ArrayList<NotificationFragmentModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        val currentNotification = notificationList[position]
        holder.textViewTitle.text = currentNotification.title
        holder.textViewDescription.text = currentNotification.description
        holder.textDate.text = currentNotification.date.split(" ")[1]
        holder.textMonth.text = currentNotification.date.split(" ")[0]
    }

    fun setNotifications(notifications: ArrayList<NotificationFragmentModel>) {
        this.notificationList = notifications
        notifyDataSetChanged()
    }

    class NotificationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textViewTitle: TextView = itemView.findViewById(R.id.notificationTitle)
        var textViewDescription: TextView = itemView.findViewById(R.id.notificationDescription)
        var textDate: TextView = itemView.findViewById(R.id.notificationDate)
        val textMonth: TextView = itemView.findViewById(R.id.notificationMonth)
    }
}