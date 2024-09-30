package com.example.calltoduty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper

class ResponseAdapter(private val responses: MutableList<Pair<Boolean, String>>) :
    RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        val layoutId = if (viewType == VIEW_TYPE_MESSAGE) R.layout.item_message else R.layout.item_response
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ResponseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val response = responses[position].second
        if (getItemViewType(position) == VIEW_TYPE_MESSAGE) {
            holder.animateText(response) // Animate the message
        } else {
            holder.responseText.text = response // Instantly display the response
        }
    }

    override fun getItemCount() = responses.size

    override fun getItemViewType(position: Int): Int {
        return if (responses[position].first) VIEW_TYPE_MESSAGE else VIEW_TYPE_RESPONSE
    }

    inner class ResponseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val responseText: TextView = itemView.findViewById(R.id.response_text)

        fun animateText(text: String) {
            val handler = Handler(Looper.getMainLooper())
            responseText.text = ""
            var index = 0

            val runnable = object : Runnable {
                override fun run() {
                    if (index < text.length) {
                        responseText.append(text[index].toString())
                        index++
                        handler.postDelayed(this, 60) //Delay of the message
                    }
                }
            }
            handler.post(runnable)
        }
    }

    companion object {
        private const val VIEW_TYPE_MESSAGE = 0
        private const val VIEW_TYPE_RESPONSE = 1
    }
}