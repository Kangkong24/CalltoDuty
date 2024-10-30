package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class CreditsFragment : DialogFragment() {
    // Declare a UI component for the close button
    private lateinit var closeButton: ImageView

    // onCreateView is called to inflate the layout for this fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    // onViewCreated is called immediately after onCreateView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the close button by finding it by its ID in the layout
        closeButton = view.findViewById(R.id.closeButton)

        // Set up close button functionality to dismiss the dialog
        closeButton.setOnClickListener {
            dismiss() // Dismiss the popup dialog
        }

        // Add your credits logic here (e.g., display names, roles, etc.)
    }

    // Companion object for creating a new instance of the fragment
    companion object {
        fun newInstance(): CreditsFragment {
            return CreditsFragment() // Return a new instance of CreditsFragment
        }
    }
}
