package com.psucoders.shuttler.utils.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.psucoders.shuttler.R
import com.psucoders.shuttler.data.model.NotificationFragmentModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter : RecyclerView.Adapter<NotificationAdapter.NotificationHolder>() {

    private var notificationList = ArrayList<NotificationFragmentModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: NotificationHolder, position: Int) {
        val currentNotification = notificationList[position]
        holder.textViewTitle.text = currentNotification.title
        holder.textViewDescription.text = currentNotification.description
        // TODO: Improve date mm/dd/yy
//        val test = currentNotification.timeStamp
//        val sfd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")
//        Log.d("NotificationAdapter", sfd.format(Date(test)))
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