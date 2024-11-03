package com.example.calltoduty

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

// Constants for argument keys
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// DialogFragment for showing a warning message about changing user data
class ChangingWarningFragment : DialogFragment() {
    // Parameters to hold argument values
    private var param1: String? = null
    private var param2: String? = null

    // Called when the fragment is created
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            // Retrieve values from the arguments bundle
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    // Inflate the fragment's view layout and set up UI components
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_changing_warning, container, false)

        // Get reference to the "Got it" button
        val gotitBtn: ImageView? = view?.findViewById(R.id.gotitButton)

        // Set an onClick listener to dismiss the dialog when the button is clicked
        gotitBtn?.setOnClickListener {
            dismiss() // Dismiss the dialog
        }
        return view
    }

    // Create a non-cancelable dialog
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    // Companion object to create a new instance of the fragment with parameters
    companion object {
        // Function to create a new instance of the fragment
        fun newInstance(param1: String, param2: String) =
            ChangingWarningFragment().apply {
                arguments = Bundle().apply {
                    // Set the arguments for the fragment
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
