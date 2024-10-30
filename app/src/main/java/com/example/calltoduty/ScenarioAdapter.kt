package com.example.calltoduty

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adapter for displaying scenarios in a RecyclerView
class ScenarioAdapter(
    private val scenarios: List<EmergencyScenario>, // List of scenarios to display
    private val onScenarioSelected: (EmergencyScenario) -> Unit // Lambda function to handle scenario selection
) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>() {

    // Method to create new ViewHolder instances
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        // Inflate the layout for each scenario item from XML
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scenario, parent, false)
        return ScenarioViewHolder(view) // Return a new instance of ScenarioViewHolder
    }

    // Method to bind data to the ViewHolder
    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = scenarios[position] // Get the scenario at the current position
        holder.bind(scenario) // Bind the scenario data to the ViewHolder
    }

    // Method to get the total number of scenarios
    override fun getItemCount(): Int = scenarios.size // Return the size of the scenarios list

    // Inner class to hold the views for each scenario item
    inner class ScenarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scenarioNameTextView: TextView = itemView.findViewById(R.id.scenarioNameTextView) // TextView for scenario name
        private val scenarioDescriptionTextView: TextView = itemView.findViewById(R.id.scenarioDescriptionTextView) // TextView for scenario description

        // Method to bind scenario data to the views
        fun bind(scenario: EmergencyScenario) {
            // Log binding information for debugging
            Log.d("ScenarioAdapter", "Binding ${scenario.scenarioName} with unlocked status: ${scenario.isUnlocked}")

            // Set the scenario name and description
            scenarioNameTextView.text = scenario.scenarioName
            scenarioDescriptionTextView.text = "Difficulty: ${scenario.difficulty}"

            // Apply UI changes based on unlock status
            itemView.apply {
                alpha = if (scenario.isUnlocked) 1.0f else 0.5f // Set transparency based on unlock status
                isClickable = scenario.isUnlocked // Make item clickable only if unlocked
                setOnClickListener {
                    // Trigger the scenario selection lambda if the scenario is unlocked
                    if (scenario.isUnlocked) onScenarioSelected(scenario)
                }
            }
        }
    }
}
