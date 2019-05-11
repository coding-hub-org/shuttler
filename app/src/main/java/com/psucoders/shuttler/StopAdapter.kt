package com.psucoders.shuttler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.w3c.dom.Text

class StopAdapter(private val stops: ArrayList<String>): RecyclerView.Adapter<StopAdapter.StopHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StopHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.shuttle_stop_card, parent, false)
        return StopHolder(itemView)
    }

    override fun getItemCount() = stops.size

    override fun onBindViewHolder(holder: StopHolder, position: Int) {
        val stop = stops[position]
        holder.stopName.text = stop
        holder.stopTime.text = "10 mins"
    }

    class StopHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val stopName: TextView = itemView.findViewById(R.id.stop_name)
        val stopTime: TextView = itemView.findViewById(R.id.stop_time)
    }
}