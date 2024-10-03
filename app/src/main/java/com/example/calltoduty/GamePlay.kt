package com.example.calltoduty

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


@Suppress("DEPRECATION")
class GamePlay : AppCompatActivity() {

    private lateinit var responseAdapter: ResponseAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var optionButton1: Button
    private lateinit var optionButton2: Button
    private lateinit var optionButton3: Button

    private lateinit var optionImage1: ImageView
    private lateinit var optionImage2: ImageView
    private lateinit var optionImage3: ImageView

    private var score: Int = 0
    private var wrongChoices: Int = 0
    private val maxWrongChoices = 4

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
        optionImage1 = findViewById(R.id.optionImage1)
        optionImage2 = findViewById(R.id.optionImage2)
        optionImage3 = findViewById(R.id.optionImage3)

        responseAdapter = ResponseAdapter(previousResponses)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = responseAdapter

        // Retrieve the selected scenario from the Intent
        chosenEmergencyScenario = intent.getParcelableExtra("selectedScenario")

        // Start the game with the selected scenario
        chosenEmergencyScenario?.let {
            startGame(it)
        }

        optionButton1.setOnClickListener { handleChoice(0) }
        optionButton2.setOnClickListener { handleChoice(1) }
        optionButton3.setOnClickListener { handleChoice(2) }

        optionImage1.setOnClickListener { handleChoice(0) }
        optionImage2.setOnClickListener { handleChoice(1) }
        optionImage3.setOnClickListener { handleChoice(2) }
    }

    private fun startGame(scenario: EmergencyScenario) {
        score = 0
        wrongChoices = 0
        chosenEmergencyScenario = scenario
        currentStep = 0
        showScenario()
    }

    private fun showScenario() {
        chosenEmergencyScenario?.let { scenario ->
            if (currentStep < scenario.steps.size) {
                val currentDialogue = scenario.steps[currentStep]

                // Add the scenario message to the list of previous responses
                previousResponses.add(Pair(true, currentDialogue.message))
                responseAdapter.notifyItemInserted(previousResponses.size - 1)
                recyclerView.post {
                    recyclerView.scrollToPosition(previousResponses.size - 1)
                }

                // Check if this step uses text or image options
                currentDialogue.textOptions?.let { textOptions ->
                    if (textOptions.size >= 3) {
                        // Set the options for the buttons (text options)
                        optionButton1.text = textOptions[0]
                        optionButton2.text = textOptions[1]
                        optionButton3.text = textOptions[2]

                        // Ensure buttons are visible and images hidden
                        setVisibilityForButtons(View.VISIBLE)
                        setVisibilityForImages(View.GONE)
                    } else {
                        showMessage("Error: Not enough text options provided.")
                    }
                } ?: currentDialogue.imageOptions?.let { imageOptions ->
                    if (imageOptions.size >= 3) {
                        // Set the options for the image buttons (image options)
                        optionImage1.setImageResource(imageOptions[0])
                        optionImage2.setImageResource(imageOptions[1])
                        optionImage3.setImageResource(imageOptions[2])

                        // Ensure images are visible and buttons hidden
                        setVisibilityForButtons(View.GONE)
                        setVisibilityForImages(View.VISIBLE)
                    } else {
                        showMessage("Error: Not enough image options provided.")
                    }
                } ?: showMessage("Error: No options provided for this dialogue step.")
            } else {
                showMessage("No more steps in this scenario.")
                endGame(success = true)
            }
        }
    }

    private fun setVisibilityForButtons(visibility: Int) {
        optionButton1.visibility = visibility
        optionButton2.visibility = visibility
        optionButton3.visibility = visibility
    }

    private fun setVisibilityForImages(visibility: Int) {
        optionImage1.visibility = visibility
        optionImage2.visibility = visibility
        optionImage3.visibility = visibility
    }

    private fun handleChoice(choice: Int) {
        val scenario = chosenEmergencyScenario ?: return
        val currentDialogue = scenario.steps.getOrNull(currentStep) ?: return

        // Check if the current step has text or image options
        val chosenOptionText = currentDialogue.textOptions?.getOrNull(choice)
        val chosenOptionImageRes = currentDialogue.imageOptions?.getOrNull(choice)

        when {
            chosenOptionText != null -> processChoice(currentDialogue, choice, chosenOptionText)
            chosenOptionImageRes != null -> processChoice(currentDialogue, choice, "Image option $choice selected")
            else -> showMessage("Invalid choice!")
        }
    }

    private fun processChoice(currentDialogue: Dialogue, choice: Int, chosenOptionText: String) {
        // Get the response message based on the user's choice
        //val responseMessage = currentDialogue.responseMessages?.get(choice) ?: "Invalid choice"

        // Add the response message to the list of previous responses
        previousResponses.add(Pair(false, chosenOptionText))
        responseAdapter.notifyItemInserted(previousResponses.size - 1)

        if (currentDialogue.correctOption.contains(choice)) {
            score++
            showMessage("Correct! You've handled it well.")
        } else {
            wrongChoices++
            showMessage("Incorrect! The correct response was one of: ${currentDialogue.correctOption.map { it + 1 }}")
        }

        if (wrongChoices >= maxWrongChoices) {
            endGame(success = false)
        } else {
            currentStep++  // Increment before showing the next scenario
            if (currentStep < (chosenEmergencyScenario?.steps?.size ?: 0)) {
                // Get the next step dialogue
                chosenEmergencyScenario?.steps?.getOrNull(currentStep)?.let { nextDialogue ->
                    // Get the response message for the current choice, fallback to the original nextDialogue message
                    val responseMessage = currentDialogue.responseMessages?.get(choice)
                    val updatedMessage = responseMessage ?: nextDialogue.message

                    // Update the next dialogue with the response message
                    val updatedDialogue = nextDialogue.copy(message = updatedMessage)

                    // Replace the step in the list with the updated dialogue
                    chosenEmergencyScenario?.steps =
                        chosenEmergencyScenario!!.steps.toMutableList().apply {
                            set(currentStep, updatedDialogue)
                        }
                }
                showScenario()  // Show the next step
            } else {
                endGame(success = true)
            }

        }
    }





    private fun showMessage(message: String) {
        // Display the message in  UI, e.g., in a TextView
    }


    private fun endGame(success: Boolean) {
        if (success) {
            showMessage("You successfully helped the caller. Your score: $score")
            val successFragment = SuccessFragment.newInstance("param1", "param2")
            successFragment.show(supportFragmentManager, "successFragment")
        } else {
            showMessage("Game over - Too many wrong responses. Your score: $score")
            val failedFragment = FailedFragment.newInstance("param1", "param2")
            failedFragment.show(supportFragmentManager, "failedFragment")
        }
    }

}
