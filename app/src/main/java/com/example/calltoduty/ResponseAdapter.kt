package com.example.calltoduty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ResponseAdapter(private val responses: MutableList<Pair<Boolean, String>>) :
    RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_MESSAGE) R.layout.item_message else R.layout.item_response
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ResponseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val response = responses[position].second
        holder.responseText.text = response
    }

    override fun getItemCount() = responses.size

    override fun getItemViewType(position: Int): Int {
        return if (responses[position].first) VIEW_TYPE_MESSAGE else VIEW_TYPE_RESPONSE
    }

    inner class ResponseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val responseText: TextView = itemView.findViewById(R.id.response_text)
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE = 0
        private const val VIEW_TYPE_RESPONSE = 1
    }
}