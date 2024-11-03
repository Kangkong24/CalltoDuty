package com.example.calltoduty

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter for the RecyclerView to display emergency scenarios
class ScenarioAdapter(
    private val scenarios: List<EmergencyScenario>,
    private val onScenarioSelected: (EmergencyScenario) -> Unit
) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>() {

    // Create a new ViewHolder to represent an item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scenario, parent, false)
        return ScenarioViewHolder(view)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = scenarios[position]
        holder.bind(scenario)
    }

    // Return the total number of items in the dataset
    override fun getItemCount(): Int = scenarios.size

    // ViewHolder class to hold and manage a single item view
    inner class ScenarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scenarioNameTextView: TextView = itemView.findViewById(R.id.scenarioNameTextView)
        //private val scenarioDescriptionTextView: TextView = itemView.findViewById(R.id.scenarioDescriptionTextView)
        private val scenarioImageView: ImageView = itemView.findViewById(R.id.scenarioImageView)

        // Bind the scenario data to the UI components
        fun bind(scenario: EmergencyScenario) {
            Log.d("ScenarioAdapter", "Binding ${scenario.scenarioName} with unlocked status: ${scenario.isUnlocked}")
            scenarioNameTextView.text = scenario.scenarioName
            //scenarioDescriptionTextView.text = "Difficulty: ${scenario.difficulty}"

            // Check if the scenario is unlocked or not
            if (!scenario.isUnlocked) {
                scenarioImageView.setImageResource(R.drawable.lock) // Display lock icon for locked scenarios
                scenarioImageView.visibility = View.VISIBLE
                scenarioNameTextView.visibility = View.GONE
            } else {
                scenarioNameTextView.visibility = View.VISIBLE // Display scenario name for unlocked scenarios
                scenarioImageView.visibility = View.GONE
            }
                itemView.apply {
                alpha = if (scenario.isUnlocked) 1.0f else 0.5f // Change transparency based on unlock status
                isClickable = scenario.isUnlocked // Enable click only if the scenario is unlocked
                setOnClickListener {
                    if (scenario.isUnlocked) onScenarioSelected(scenario) // Handle scenario selection
                }
            }
        }
    }
}
