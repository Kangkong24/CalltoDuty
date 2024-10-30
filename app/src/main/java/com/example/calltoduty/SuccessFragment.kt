package com.example.calltoduty

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

// Constants for fragment arguments
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// DialogFragment for displaying a success message and moving to the next level
class SuccessFragment : DialogFragment() {
    private var param1: String? = null // First parameter for fragment
    private var param2: String? = null // Second parameter for fragment

    // Called when the fragment is created to retrieve arguments
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1) // Get param1 from arguments
            param2 = it.getString(ARG_PARAM2) // Get param2 from arguments
        }
    }

    // Called to create the view hierarchy associated with the fragment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_success, container, false)

        // Find the next level button and set a click listener
        val nextLevelButton: ImageView = view.findViewById(R.id.nextLevelButton)
        nextLevelButton.setOnClickListener {
            // Communicate with GamePlay activity to load the next scenario
            (activity as? GamePlay)?.loadNextScenario()
            // Dismiss the dialog when the button is clicked
            dismiss()
        }

        return view // Return the inflated view
    }

    // Static method to create a new instance of the fragment with arguments
    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SuccessFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1) // Put param1 in the bundle
                    putString(ARG_PARAM2, param2) // Put param2 in the bundle
                }
            }
    }
}
