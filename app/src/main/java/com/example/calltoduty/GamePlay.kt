package com.example.calltoduty

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.random.Random

class GamePlay : AppCompatActivity() {

    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var optionButton1: Button
    private lateinit var optionButton2: Button
    private lateinit var optionButton3: Button

    private var score: Int = 0
    private var wrongChoices: Int = 0
    private val maxWrongChoices = 3

    private val previousResponses: MutableList<Pair<Boolean, String>> = mutableListOf()

    private var chosenEmergencyScenario: EmergencyScenario? = null
    private var currentStep = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_play)

        recyclerView = findViewById(R.id.recyclerView)
        optionButton1 = findViewById(R.id.optionButton1)
        optionButton2 = findViewById(R.id.optionButton2)
        optionButton3 = findViewById(R.id.optionButton3)

        responseAdapter = ResponseAdapter(previousResponses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = responseAdapter

        // Retrieve the difficulty from the Intent
        val difficultyString = intent.getStringExtra("difficulty")
        val selectedDifficulty = Difficulty.valueOf(difficultyString!!)

        // Start the game with the selected difficulty
        startGame(selectedDifficulty)

        optionButton1.setOnClickListener { handleChoice(0) }
        optionButton2.setOnClickListener { handleChoice(1) }
        optionButton3.setOnClickListener { handleChoice(2) }
    }

    private fun startGame(difficulty: Difficulty) {
        score = 0
        wrongChoices = 0

        // Filter the scenarios by the selected difficulty
        val filteredScenarios = emergencyScenarios.filter { it.difficulty == difficulty }

        if (filteredScenarios.isNotEmpty()) {
            // Randomly select a scenario from the filtered list
            chosenEmergencyScenario = filteredScenarios[Random.nextInt(filteredScenarios.size)]
            currentStep = 0
            showScenario()
        } else {
            showMessage("No emergencies available.")
            endGame(success = false)
        }
    }

    private fun showScenario() {
        chosenEmergencyScenario?.let { scenario ->
            if (currentStep < scenario.steps.size) {
                val currentDialogue = scenario.steps[currentStep]

                // Add the scenario message to the list of previous responses
                previousResponses.add(Pair(true, currentDialogue.message))
                responseAdapter.notifyItemInserted(previousResponses.size - 1)

                // Set the options for the buttons
                optionButton1.text = currentDialogue.options[0]
                optionButton2.text = currentDialogue.options[1]
                optionButton3.text = currentDialogue.options[2]
            } else {
                showMessage("No more steps in this scenario.")
                endGame(success = true)
            }
        }
    }

    private fun handleChoice(choice: Int) {
        val scenario = chosenEmergencyScenario ?: return
        val currentDialogue = scenario.steps[currentStep]
        val chosenOptionText = currentDialogue.options[choice]

        if (currentDialogue.correctOption.contains(choice)) {
            score++
            showMessage("Correct! You've handled it well.")
        } else {
            wrongChoices++
            showMessage("Incorrect! The correct response was one of: ${currentDialogue.correctOption.map { it + 1 }}")
        }

        // Add the chosen option to the list of previous responses
        previousResponses.add(Pair(false, chosenOptionText))
        responseAdapter.notifyItemInserted(previousResponses.size - 1)

        if (wrongChoices >= maxWrongChoices) {
            endGame(success = false)
        } else {
            currentStep++
            if (currentStep < scenario.steps.size) {
                showScenario()
            } else {
                endGame(success = true)
            }
        }
    }

    private fun showMessage(message: String) {
        // Display the message in your UI, e.g., in a TextView
    }

    private fun endGame(success: Boolean) {
        if (success) {
            showMessage("You successfully helped the caller. Your score: $score")
        } else {
            showMessage("Game over - Too many wrong responses. Your score: $score")
        }

        // Adjust for window insets (if necessary)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
