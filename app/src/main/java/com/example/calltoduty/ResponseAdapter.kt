package com.example.calltoduty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.os.Handler
import android.os.Looper

// Adapter for managing and displaying responses in a RecyclerView
class ResponseAdapter(private val responses: MutableList<Pair<Boolean, String>>) :
    RecyclerView.Adapter<ResponseAdapter.ResponseViewHolder>() {

    // Method to create new ViewHolder instances based on the response type
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResponseViewHolder {
        // Determine the layout ID based on the view type (message or response)
        val layoutId = if (viewType == VIEW_TYPE_MESSAGE) R.layout.item_message else R.layout.item_response
        // Inflate the selected layout and create a new ViewHolder instance
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return ResponseViewHolder(view) // Return the new ViewHolder
    }

    // Method to bind data to the ViewHolder
    override fun onBindViewHolder(holder: ResponseViewHolder, position: Int) {
        val response = responses[position].second // Get the response text from the list

        // Check the view type to decide how to display the response
        if (getItemViewType(position) == VIEW_TYPE_MESSAGE) {
            holder.animateText(response) // Animate the message display
        } else {
            holder.responseText.text = response // Instantly display the response
        }
    }

    // Method to get the total number of responses
    override fun getItemCount() = responses.size // Return the size of the responses list

    // Method to determine the view type for a given position
    override fun getItemViewType(position: Int): Int {
        // Return VIEW_TYPE_MESSAGE if the first element of the Pair is true, otherwise return VIEW_TYPE_RESPONSE
        return if (responses[position].first) VIEW_TYPE_MESSAGE else VIEW_TYPE_RESPONSE
    }

    // Inner class to hold the views for each response item
    inner class ResponseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val responseText: TextView = itemView.findViewById(R.id.response_text) // TextView for displaying the response text

        // Method to animate the text display for messages
        fun animateText(text: String) {
            val handler = Handler(Looper.getMainLooper()) // Handler to manage the animation on the main thread
            responseText.text = "" // Clear the TextView before starting the animation
            var index = 0 // Index to track the current character being appended

            // Runnable to append characters one by one with a delay
            val runnable = object : Runnable {
                override fun run() {
                    if (index < text.length) { // Check if there are still characters to append
                        responseText.append(text[index].toString()) // Append the current character to the TextView
                        index++ // Move to the next character
                        handler.postDelayed(this, 60) // Delay before appending the next character
                    }
                }
            }
            handler.post(runnable) // Start the text animation
        }
    }

    // Companion object to define constant view types
    companion object {
        private const val VIEW_TYPE_MESSAGE = 0 // Constant for message view type
        private const val VIEW_TYPE_RESPONSE = 1 // Constant for response view type
    }
}
