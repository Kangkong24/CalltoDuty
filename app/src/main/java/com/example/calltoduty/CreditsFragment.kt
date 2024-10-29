package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class CreditsFragment : DialogFragment() {
    private lateinit var closeButton: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_credits, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeButton = view.findViewById(R.id.closeButton)

        // Set up close button functionality
        closeButton.setOnClickListener {
            dismiss() // Dismiss the popup
        }

        // Add your credits logic here
    }

    companion object {
        fun newInstance(): CreditsFragment {
            return CreditsFragment()
        }
    }
}
