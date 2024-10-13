package com.example.calltoduty

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FailedFragment : DialogFragment() {
    private var param1: String? = null
    private var param2: String? = null

    // Define an interface for communication with the activity
    interface FailedFragmentListener {
        fun onPlayAgain()
    }
    private var listener: FailedFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }


        // Ensure the host activity implements the listener interface
        if (activity is FailedFragmentListener) {
            listener = activity as FailedFragmentListener
        } else {
            throw RuntimeException("$activity must implement FailedFragmentListener")
        }
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_failed, container, false)

        // Find the button and set a click listener
        val playAgainButton: ImageView = view.findViewById(R.id.playAgainButton)
        playAgainButton.setOnClickListener {

            // Notify the activity to restart the game
            listener?.onPlayAgain()
            // Dismiss the dialog when the button is clicked
            dismiss()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FailedFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}