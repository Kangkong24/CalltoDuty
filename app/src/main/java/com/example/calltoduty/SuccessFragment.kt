package com.example.calltoduty

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class SuccessFragment : DialogFragment() {
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_success, container, false)

        // Find the button and set a click listener
        val nextLevelButton: ImageView = view.findViewById(R.id.nextLevelButton)
        nextLevelButton.setOnClickListener {
            // Communicate with GamePlay activity to load the next scenario
            (activity as? GamePlay)?.loadNextScenario()
            // Dismiss the dialog when the button is clicked
            dismiss()
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SuccessFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
