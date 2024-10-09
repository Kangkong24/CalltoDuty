package com.example.calltoduty

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScenarioAdapter(
    private val scenarios: List<EmergencyScenario>,
    private val onScenarioSelected: (EmergencyScenario, Int) -> Unit
) : RecyclerView.Adapter<ScenarioAdapter.ScenarioViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScenarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_scenario, parent, false)
        return ScenarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScenarioViewHolder, position: Int) {
        val scenario = scenarios[position]
        holder.bind(scenario, position)
    }

    override fun getItemCount(): Int = scenarios.size

    inner class ScenarioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scenarioNameTextView: TextView = itemView.findViewById(R.id.scenarioNameTextView)
        private val scenarioDescriptionTextView: TextView = itemView.findViewById(R.id.scenarioDescriptionTextView)

        fun bind(scenario: EmergencyScenario, position: Int) {
            scenarioNameTextView.text = scenario.scenarioName
            scenarioDescriptionTextView.text = scenario.difficulty.toString()
            itemView.setOnClickListener { onScenarioSelected(scenario, position) }
        }
    }
}
